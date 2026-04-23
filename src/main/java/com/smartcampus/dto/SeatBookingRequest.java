package com.smartcampus.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SeatBookingRequest {
    private Long eventId;
    private String seatLabel;
    private Long studentId;
}
