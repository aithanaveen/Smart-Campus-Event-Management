package com.smartcampus.service;

import com.smartcampus.entity.Event;
import com.smartcampus.repository.EventRepository;
import com.smartcampus.repository.RegistrationRepository;
import com.smartcampus.repository.SeatRepository;
import com.smartcampus.entity.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChatbotService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Value("${openai.api.key:not-configured}")
    private String openaiApiKey;

    public String processMessage(String message) {
        String lowerMsg = message.toLowerCase().trim();

        if (lowerMsg.contains("upcoming") || lowerMsg.contains("event") && lowerMsg.contains("list")) {
            return getUpcomingEventsResponse();
        }
        if (lowerMsg.contains("seat") && (lowerMsg.contains("available") || lowerMsg.contains("availability"))) {
            return getSeatAvailabilityResponse();
        }
        if (lowerMsg.contains("venue") || lowerMsg.contains("location") || lowerMsg.contains("where")) {
            return getVenueResponse();
        }
        if (lowerMsg.contains("deadline") || lowerMsg.contains("last date") || lowerMsg.contains("register")) {
            return getDeadlineResponse();
        }
        if (lowerMsg.contains("certificate") || lowerMsg.contains("download")) {
            return getCertificateResponse();
        }
        if (lowerMsg.contains("time") || lowerMsg.contains("timing") || lowerMsg.contains("when") || lowerMsg.contains("schedule")) {
            return getTimingResponse();
        }
        if (lowerMsg.contains("help") || lowerMsg.contains("hi") || lowerMsg.contains("hello")) {
            return getHelpResponse();
        }
        if (lowerMsg.contains("category") || lowerMsg.contains("type")) {
            return getCategoriesResponse();
        }
        if (lowerMsg.contains("contact") || lowerMsg.contains("support")) {
            return "📧 For support, email us at admin@smartcampus.com or visit the admin office during working hours (9 AM - 5 PM).";
        }

        return getFallbackResponse();
    }

    private String getUpcomingEventsResponse() {
        List<Event> events = eventRepository.findUpcomingEvents(LocalDate.now());
        if (events.isEmpty()) {
            return "📅 No upcoming events at the moment. Check back soon!";
        }
        StringBuilder sb = new StringBuilder("📅 **Upcoming Events:**\n\n");
        for (Event e : events) {
            sb.append("🎯 **").append(e.getTitle()).append("**\n");
            sb.append("   📆 ").append(e.getEventDate()).append(" | 🏛️ ").append(e.getVenue());
            sb.append(" | 💺 ").append(e.getAvailableSeats()).append(" seats left\n\n");
        }
        return sb.toString();
    }

    private String getSeatAvailabilityResponse() {
        List<Event> events = eventRepository.findUpcomingEvents(LocalDate.now());
        StringBuilder sb = new StringBuilder("💺 **Seat Availability:**\n\n");
        for (Event e : events) {
            long available = seatRepository.countByEventIdAndStatus(e.getId(), Seat.SeatStatus.AVAILABLE);
            sb.append("🎯 ").append(e.getTitle()).append(": **").append(available).append("** seats available out of ").append(e.getTotalSeats()).append("\n");
        }
        return sb.toString();
    }

    private String getVenueResponse() {
        List<Event> events = eventRepository.findUpcomingEvents(LocalDate.now());
        StringBuilder sb = new StringBuilder("🏛️ **Event Venues:**\n\n");
        for (Event e : events) {
            sb.append("🎯 ").append(e.getTitle()).append("\n");
            sb.append("   📍 ").append(e.getVenue()).append(" - ").append(e.getVenueAddress()).append("\n\n");
        }
        return sb.toString();
    }

    private String getDeadlineResponse() {
        List<Event> events = eventRepository.findUpcomingEvents(LocalDate.now());
        StringBuilder sb = new StringBuilder("⏰ **Registration Deadlines:**\n\n");
        for (Event e : events) {
            sb.append("🎯 ").append(e.getTitle()).append(": ");
            if (e.getRegistrationDeadline() != null) {
                sb.append(e.getRegistrationDeadline().toLocalDate());
            } else {
                sb.append("Open until event day");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String getTimingResponse() {
        List<Event> events = eventRepository.findUpcomingEvents(LocalDate.now());
        StringBuilder sb = new StringBuilder("🕐 **Event Timings:**\n\n");
        for (Event e : events) {
            sb.append("🎯 ").append(e.getTitle()).append("\n");
            sb.append("   📆 ").append(e.getEventDate()).append(" | ⏰ ").append(e.getStartTime()).append(" - ").append(e.getEndTime()).append("\n\n");
        }
        return sb.toString();
    }

    private String getCertificateResponse() {
        return "🎓 **Certificate Information:**\n\n" +
                "• Certificates are generated after event completion\n" +
                "• You must attend the event (QR verified) to receive a certificate\n" +
                "• Download from your Student Dashboard → My Registrations\n" +
                "• PDF certificates include your name, event details, and unique code";
    }

    private String getCategoriesResponse() {
        List<String> categories = eventRepository.findAllCategories();
        StringBuilder sb = new StringBuilder("📂 **Event Categories:**\n\n");
        for (String cat : categories) {
            sb.append("• ").append(cat).append("\n");
        }
        return sb.toString();
    }

    private String getHelpResponse() {
        return "👋 **Hello! I'm the Smart Campus Assistant.**\n\n" +
                "I can help you with:\n\n" +
                "📅 **Upcoming events** - Ask about events\n" +
                "💺 **Seat availability** - Check available seats\n" +
                "🏛️ **Venue details** - Find event locations\n" +
                "⏰ **Event timings** - Know when events happen\n" +
                "📝 **Registration deadlines** - Don't miss the deadline\n" +
                "🎓 **Certificate status** - Download certificates\n" +
                "📂 **Event categories** - Browse by category\n\n" +
                "Just type your question!";
    }

    private String getFallbackResponse() {
        return "🤔 I'm not sure I understand. Try asking about:\n" +
                "• Upcoming events\n• Seat availability\n• Venue details\n" +
                "• Event timings\n• Registration deadlines\n• Certificates\n\n" +
                "Type **help** for more options.";
    }
}
