package com.smartcampus.repository;

import com.smartcampus.entity.Seat;
import com.smartcampus.entity.Seat.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByEventIdOrderBySeatRowAscSeatColAsc(Long eventId);
    List<Seat> findByEventIdAndStatus(Long eventId, SeatStatus status);
    Optional<Seat> findByEventIdAndSeatLabel(Long eventId, String seatLabel);
    long countByEventIdAndStatus(Long eventId, SeatStatus status);
    void deleteByEventId(Long eventId);
}
