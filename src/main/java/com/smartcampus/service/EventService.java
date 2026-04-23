package com.smartcampus.service;

import com.smartcampus.entity.Event;
import com.smartcampus.entity.Event.EventStatus;
import com.smartcampus.entity.Seat;
import com.smartcampus.repository.EventRepository;
import com.smartcampus.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SeatRepository seatRepository;

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> findUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDate.now());
    }

    public List<Event> searchEvents(String keyword) {
        return eventRepository.searchEvents(keyword);
    }

    public List<Event> findByCategory(String category) {
        return eventRepository.findUpcomingByCategory(category, LocalDate.now());
    }

    public List<Event> findByDepartment(String department) {
        return eventRepository.findUpcomingByDepartment(department, LocalDate.now());
    }

    public List<String> getAllCategories() {
        return eventRepository.findAllCategories();
    }

    public List<String> getAllDepartments() {
        return eventRepository.findAllDepartments();
    }

    @Transactional
    public Event createEvent(Event event) {
        event.setAvailableSeats(event.getTotalSeats());
        Event savedEvent = eventRepository.save(event);

        // Generate seats
        if (event.getSeatRows() > 0 && event.getSeatColumns() > 0) {
            generateSeats(savedEvent);
        }

        return savedEvent;
    }

    @Transactional
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public long countByStatus(EventStatus status) {
        return eventRepository.countByStatus(status);
    }

    public long count() {
        return eventRepository.count();
    }

    private void generateSeats(Event event) {
        List<Seat> seats = new ArrayList<>();
        for (int r = 1; r <= event.getSeatRows(); r++) {
            for (int c = 1; c <= event.getSeatColumns(); c++) {
                String label = (char) ('A' + r - 1) + String.valueOf(c);
                Seat seat = Seat.builder()
                        .event(event)
                        .seatLabel(label)
                        .seatRow(r)
                        .seatCol(c)
                        .status(Seat.SeatStatus.AVAILABLE)
                        .seatType(r <= 2 ? "VIP" : "Regular")
                        .build();
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }
}
