package com.smartcampus.service;

import com.smartcampus.entity.*;
import com.smartcampus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SeatService seatService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Registration registerForEvent(Student student, Long eventId, String seatLabel) {
        // Check if already registered
        if (registrationRepository.existsByStudentIdAndEventId(student.getId(), eventId)) {
            throw new RuntimeException("Already registered for this event");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats available");
        }

        // Book the seat
        seatService.bookSeat(eventId, seatLabel, student);

        // Create registration
        String registrationCode = "REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Registration registration = Registration.builder()
                .student(student)
                .event(event)
                .seatNumber(seatLabel)
                .registrationCode(registrationCode)
                .status(Registration.RegistrationStatus.CONFIRMED)
                .build();

        // Generate QR Code
        try {
            String qrData = String.format("StudentID:%s|EventID:%d|Seat:%s|RegCode:%s",
                    student.getStudentId(), eventId, seatLabel, registrationCode);
            String qrPath = qrCodeService.generateQRCode(qrData, registrationCode);
            registration.setQrCodePath(qrPath);
        } catch (Exception e) {
            System.err.println("QR Code generation failed: " + e.getMessage());
        }

        // Update available seats
        event.setAvailableSeats(event.getAvailableSeats() - 1);
        eventRepository.save(event);

        Registration savedReg = registrationRepository.save(registration);

        // Send confirmation email
        try {
            emailService.sendRegistrationConfirmation(student.getEmail(), student.getFullName(),
                    event.getTitle(), seatLabel, registrationCode);
        } catch (Exception e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }

        return savedReg;
    }

    public List<Registration> findByStudentId(Long studentId) {
        return registrationRepository.findByStudentId(studentId);
    }

    public List<Registration> findByEventId(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public Optional<Registration> findByRegistrationCode(String code) {
        return registrationRepository.findByRegistrationCode(code);
    }

    public Optional<Registration> findById(Long id) {
        return registrationRepository.findById(id);
    }

    public long countByEventId(Long eventId) {
        return registrationRepository.countByEventId(eventId);
    }

    public long count() {
        return registrationRepository.count();
    }

    @Transactional
    public void cancelRegistration(Long registrationId) {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        reg.setStatus(Registration.RegistrationStatus.CANCELLED);
        registrationRepository.save(reg);

        // Free up the seat
        Event event = reg.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + 1);
        eventRepository.save(event);
    }

    public List<Registration> findConfirmedByStudent(Long studentId) {
        return registrationRepository.findConfirmedByStudent(studentId);
    }

    public List<Object[]> findTopCategoriesByStudent(Long studentId) {
        return registrationRepository.findTopCategoriesByStudent(studentId);
    }
}
