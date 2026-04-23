package com.smartcampus.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String seatLabel;

    @Column(name = "seat_row", nullable = false)
    private int seatRow;

    @Column(name = "seat_col", nullable = false)
    private int seatCol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booked_by")
    private Student bookedBy;

    private String seatType;

    public enum SeatStatus {
        AVAILABLE, BOOKED, BLOCKED
    }
}
