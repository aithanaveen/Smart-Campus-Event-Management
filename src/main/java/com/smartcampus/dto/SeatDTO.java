package com.smartcampus.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SeatDTO {
    private Long id;
    private String seatLabel;
    private int seatRow;
    private int seatCol;
    private String status;
    private String bookedByName;
    private String seatType;
}
