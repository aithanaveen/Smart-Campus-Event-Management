package com.smartcampus.controller;

import com.smartcampus.entity.Student;
import com.smartcampus.service.EmailService;
import com.smartcampus.service.RegistrationService;
import com.smartcampus.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventApiController {

    private final EmailService emailService;
    private final RegistrationService registrationService;
    private final StudentService studentService;

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        EmailService.OtpDispatchResult result = emailService.generateOTP(email);
        if (result.delivered()) {
            return ResponseEntity.ok(Map.of(
                    "delivered", true,
                    "message", "OTP sent successfully"));
        }

        return ResponseEntity.status(503).body(Map.of(
                "delivered", false,
                "message", result.message() + " Please configure MAIL_USERNAME and MAIL_PASSWORD."));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        boolean verified = emailService.verifyOTP(email, otp);
        if (verified) {
            return ResponseEntity.ok(Map.of("verified", true, "message", "OTP verified"));
        }
        return ResponseEntity.badRequest().body(Map.of("verified", false, "message", "Invalid OTP"));
    }

    @PostMapping("/register-event")
    public ResponseEntity<?> registerForEvent(@RequestBody Map<String, Object> request,
                                               Authentication auth) {
        try {
            Long eventId = Long.valueOf(request.get("eventId").toString());
            String seatLabel = request.get("seatLabel").toString();
            Student student = studentService.findByEmail(auth.getName()).orElseThrow();
            if (!emailService.hasOtpVerification(student.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Invalid or expired OTP. Please verify your email first."));
            }

            var reg = registrationService.registerForEvent(student, eventId, seatLabel);
            emailService.consumeOtpVerification(student.getEmail());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "registrationCode", reg.getRegistrationCode(),
                    "message", "Successfully registered!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
