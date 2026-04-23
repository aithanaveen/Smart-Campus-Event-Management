package com.smartcampus.controller;

import com.smartcampus.entity.*;
import com.smartcampus.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private StudentService studentService;
    @Autowired private EventService eventService;
    @Autowired private RegistrationService registrationService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private CertificateService certificateService;
    @Autowired private RecommendationService recommendationService;
    @Autowired private QRCodeService qrCodeService;

    @Value("${google.maps.api.key:}")
    private String googleMapsApiKey;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        List<Registration> registrations = registrationService.findConfirmedByStudent(student.getId());
        List<Event> recommended = recommendationService.getRecommendations(
                student.getId(), student.getDepartment(), student.getInterests());

        model.addAttribute("student", student);
        model.addAttribute("registrations", registrations);
        model.addAttribute("recommended", recommended);
        model.addAttribute("totalRegistrations", registrations.size());
        return "student/dashboard";
    }

    @GetMapping("/events")
    public String browseEvents(@RequestParam(required = false) String category,
                               @RequestParam(required = false) String department,
                               @RequestParam(required = false) String search,
                               Model model) {
        List<Event> events;
        if (search != null && !search.isEmpty()) {
            events = eventService.searchEvents(search);
        } else if (category != null && !category.isEmpty()) {
            events = eventService.findByCategory(category);
        } else if (department != null && !department.isEmpty()) {
            events = eventService.findByDepartment(department);
        } else {
            events = eventService.findUpcomingEvents();
        }
        model.addAttribute("events", events);
        model.addAttribute("categories", eventService.getAllCategories());
        model.addAttribute("departments", eventService.getAllDepartments());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("searchQuery", search);
        return "student/events";
    }

    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Authentication auth, Model model) {
        Event event = eventService.findById(id).orElseThrow();
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        boolean isRegistered = false;
        try {
            List<Registration> regs = registrationService.findByStudentId(student.getId());
            isRegistered = regs.stream().anyMatch(r -> r.getEvent().getId().equals(id)
                    && r.getStatus() == Registration.RegistrationStatus.CONFIRMED);
        } catch (Exception e) { /* ignore */ }

        model.addAttribute("event", event);
        model.addAttribute("isRegistered", isRegistered);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "student/event-details";
    }

    @GetMapping("/my-registrations")
    public String myRegistrations(Authentication auth, Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        List<Registration> registrations = registrationService.findByStudentId(student.getId());
        model.addAttribute("registrations", registrations);
        return "student/my-registrations";
    }

    @GetMapping("/registration/{regId}/qr")
    public ResponseEntity<byte[]> downloadQR(@PathVariable Long regId) {
        try {
            Registration reg = registrationService.findByRegistrationCode(null).orElse(null);
            // Generate QR from registration data
            String qrData = "RegID:" + regId;
            byte[] qrImage = java.util.Base64.getDecoder().decode(qrCodeService.generateQRCodeBase64(qrData));
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=eventpass_" + regId + ".png")
                    .body(qrImage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/certificate/{eventId}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long eventId, Authentication auth) {
        try {
            Student student = studentService.findByEmail(auth.getName()).orElseThrow();
            byte[] pdfBytes = certificateService.generateCertificate(student.getId(), eventId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam Long eventId, @RequestParam int rating,
                                 @RequestParam String comments, Authentication auth) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        Event event = eventService.findById(eventId).orElseThrow();
        Feedback feedback = Feedback.builder()
                .student(student).event(event).rating(rating).comments(comments).build();
        feedbackService.submitFeedback(feedback);
        return "redirect:/student/my-registrations?feedback=success";
    }
}
