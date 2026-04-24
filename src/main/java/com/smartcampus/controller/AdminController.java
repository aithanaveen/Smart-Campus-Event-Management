package com.smartcampus.controller;

import com.smartcampus.dto.DashboardStats;
import com.smartcampus.entity.*;
import com.smartcampus.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;
    private final StudentService studentService;
    private final RegistrationService registrationService;
    private final AttendanceService attendanceService;
    private final FeedbackService feedbackService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardStats stats = DashboardStats.builder()
                .totalEvents(eventService.count())
                .upcomingEvents(eventService.countByStatus(Event.EventStatus.UPCOMING))
                .completedEvents(eventService.countByStatus(Event.EventStatus.COMPLETED))
                .totalStudents(studentService.count())
                .totalRegistrations(registrationService.count())
                .totalAttendance(attendanceService.count())
                .build();
        model.addAttribute("stats", stats);
        model.addAttribute("events", eventService.findAll());
        return "admin/dashboard";
    }

    @GetMapping("/events")
    public String manageEvents(Model model) {
        model.addAttribute("events", eventService.findAll());
        return "admin/events";
    }

    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "admin/event-form";
    }

    @PostMapping("/events/save")
    public String saveEvent(@Valid @ModelAttribute("event") Event event,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/event-form";
        }
        try {
            if (event.getId() == null) {
                eventService.createEvent(event);
                redirectAttributes.addFlashAttribute("successMessage", "Event created successfully!");
            } else {
                eventService.updateEvent(event);
                redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/events";
    }

    @GetMapping("/events/edit/{id}")
    public String editEvent(@PathVariable Long id, Model model) {
        Event event = eventService.findById(id).orElseThrow();
        model.addAttribute("event", event);
        return "admin/event-form";
    }

    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully!");
        return "redirect:/admin/events";
    }

    @GetMapping("/events/{id}/registrations")
    public String viewRegistrations(@PathVariable Long id, Model model) {
        Event event = eventService.findById(id).orElseThrow();
        List<Registration> registrations = registrationService.findByEventId(id);

        long confirmedCount = registrations.stream()
                .filter(r -> r.getStatus() == Registration.RegistrationStatus.CONFIRMED)
                .count();
        long attendedCount = registrations.stream()
                .filter(Registration::isAttended)
                .count();

        model.addAttribute("event", event);
        model.addAttribute("registrations", registrations);
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("attendedCount", attendedCount);
        return "admin/registrations";
    }

    @GetMapping("/events/{id}/attendance")
    public String viewAttendance(@PathVariable Long id, Model model) {
        Event event = eventService.findById(id).orElseThrow();
        List<Attendance> attendanceList = attendanceService.findByEventId(id);
        model.addAttribute("event", event);
        model.addAttribute("attendanceList", attendanceList);
        return "admin/attendance";
    }

    @PostMapping("/attendance/scan")
    public String scanQR(@RequestParam String registrationCode, RedirectAttributes redirectAttributes) {
        try {
            Attendance attendance = attendanceService.markAttendance(registrationCode);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Attendance marked for: " + attendance.getStudent().getFullName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/{id}/feedback")
    public String viewFeedback(@PathVariable Long id, Model model) {
        Event event = eventService.findById(id).orElseThrow();
        List<Feedback> feedbacks = feedbackService.findByEventId(id);
        Double avgRating = feedbackService.getAverageRating(id);
        model.addAttribute("event", event);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("avgRating", avgRating != null ? avgRating : 0);
        return "admin/feedback";
    }

    @GetMapping("/students")
    public String manageStudents(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "admin/students";
    }
}
