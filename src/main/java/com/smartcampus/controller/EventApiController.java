package com.smartcampus.controller;

import com.smartcampus.entity.Student;
import com.smartcampus.service.EmailService;
import com.smartcampus.service.RegistrationService;
import com.smartcampus.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class EventApiController {

    @Autowired private EmailService emailService;
    @Autowired private RegistrationService registrationService;
    @Autowired private StudentService studentService;

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        emailService.generateOTP(email);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
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
            var reg = registrationService.registerForEvent(student, eventId, seatLabel);
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
