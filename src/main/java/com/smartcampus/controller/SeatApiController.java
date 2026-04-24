package com.smartcampus.controller;

import com.smartcampus.dto.SeatBookingRequest;
import com.smartcampus.dto.SeatDTO;
import com.smartcampus.entity.Student;
import com.smartcampus.service.SeatService;
import com.smartcampus.service.StudentService;
import com.smartcampus.service.EmailService;
import com.smartcampus.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/{eventId}")
    public ResponseEntity<List<SeatDTO>> getSeats(@PathVariable Long eventId) {
        return ResponseEntity.ok(seatService.getSeatsByEventId(eventId));
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookSeat(@RequestBody SeatBookingRequest request, Authentication auth) {
        try {
            if (request.getEventId() == null || request.getSeatLabel() == null || request.getSeatLabel().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Event and seat are required."));
            }

            Student student = studentService.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Authenticated student not found"));

            // Defensive check: when client sends studentId, ensure it matches logged-in student
            if (request.getStudentId() != null && !request.getStudentId().equals(student.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Student identity mismatch."));
            }

            if (!emailService.hasOtpVerification(student.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid or expired OTP. Please verify your email first."));
            }

            // Register and book seat
            com.smartcampus.entity.Registration registration = registrationService.registerForEvent(student,
                    request.getEventId(), request.getSeatLabel());
            emailService.consumeOtpVerification(student.getEmail());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "registrationCode", registration.getRegistrationCode(),
                    "seatLabel", request.getSeatLabel()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSeat(@RequestParam Long seatId, @RequestParam String status) {
        try {
            com.smartcampus.entity.Seat.SeatStatus seatStatus = com.smartcampus.entity.Seat.SeatStatus.valueOf(status);
            SeatDTO updated = seatService.updateSeatStatus(seatId, seatStatus);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
