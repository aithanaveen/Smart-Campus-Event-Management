package com.smartcampus.controller;

import com.smartcampus.dto.SeatBookingRequest;
import com.smartcampus.dto.SeatDTO;
import com.smartcampus.entity.Student;
import com.smartcampus.service.SeatService;
import com.smartcampus.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seats")
public class SeatApiController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/{eventId}")
    public ResponseEntity<List<SeatDTO>> getSeats(@PathVariable Long eventId) {
        return ResponseEntity.ok(seatService.getSeatsByEventId(eventId));
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookSeat(@RequestBody SeatBookingRequest request) {
        try {
            Student student = studentService.findById(request.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            SeatDTO seat = seatService.bookSeat(request.getEventId(), request.getSeatLabel(), student);
            return ResponseEntity.ok(seat);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSeat(@RequestParam Long seatId, @RequestParam String status) {
        try {
            com.smartcampus.entity.Seat.SeatStatus seatStatus =
                    com.smartcampus.entity.Seat.SeatStatus.valueOf(status);
            SeatDTO updated = seatService.updateSeatStatus(seatId, seatStatus);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
