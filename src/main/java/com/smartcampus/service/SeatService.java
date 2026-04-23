package com.smartcampus.service;

import com.smartcampus.dto.SeatDTO;
import com.smartcampus.entity.Seat;
import com.smartcampus.entity.Student;
import com.smartcampus.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<SeatDTO> getSeatsByEventId(Long eventId) {
        List<Seat> seats = seatRepository.findByEventIdOrderBySeatRowAscSeatColAsc(eventId);
        return seats.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public SeatDTO bookSeat(Long eventId, String seatLabel, Student student) {
        Optional<Seat> optSeat = seatRepository.findByEventIdAndSeatLabel(eventId, seatLabel);

        if (optSeat.isEmpty()) {
            throw new RuntimeException("Seat not found");
        }

        Seat seat = optSeat.get();
        if (seat.getStatus() != Seat.SeatStatus.AVAILABLE) {
            throw new RuntimeException("Seat is already booked");
        }

        seat.setStatus(Seat.SeatStatus.BOOKED);
        seat.setBookedBy(student);
        Seat savedSeat = seatRepository.save(seat);

        SeatDTO dto = convertToDTO(savedSeat);

        // Broadcast seat update via WebSocket
        messagingTemplate.convertAndSend("/topic/seats/" + eventId, dto);

        return dto;
    }

    @Transactional
    public SeatDTO updateSeatStatus(Long seatId, Seat.SeatStatus status) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        seat.setStatus(status);
        if (status == Seat.SeatStatus.AVAILABLE) {
            seat.setBookedBy(null);
        }
        Seat savedSeat = seatRepository.save(seat);

        SeatDTO dto = convertToDTO(savedSeat);
        messagingTemplate.convertAndSend("/topic/seats/" + seat.getEvent().getId(), dto);

        return dto;
    }

    public long getAvailableSeatsCount(Long eventId) {
        return seatRepository.countByEventIdAndStatus(eventId, Seat.SeatStatus.AVAILABLE);
    }

    private SeatDTO convertToDTO(Seat seat) {
        return SeatDTO.builder()
                .id(seat.getId())
                .seatLabel(seat.getSeatLabel())
                .seatRow(seat.getSeatRow())
                .seatCol(seat.getSeatCol())
                .status(seat.getStatus().name())
                .bookedByName(seat.getBookedBy() != null ? seat.getBookedBy().getFullName() : null)
                .seatType(seat.getSeatType())
                .build();
    }
}
