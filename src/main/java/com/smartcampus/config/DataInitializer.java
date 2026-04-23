package com.smartcampus.config;

import com.smartcampus.entity.Admin;
import com.smartcampus.entity.Event;
import com.smartcampus.entity.Seat;
import com.smartcampus.repository.AdminRepository;
import com.smartcampus.repository.EventRepository;
import com.smartcampus.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData(AdminRepository adminRepo, EventRepository eventRepo, SeatRepository seatRepo) {
        return args -> {
            // Create default admin if not exists
            if (!adminRepo.existsByEmail("admin@smartcampus.com")) {
                Admin admin = Admin.builder()
                        .fullName("System Administrator")
                        .email("admin@smartcampus.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role("ROLE_ADMIN")
                        .department("IT")
                        .build();
                adminRepo.save(admin);
                System.out.println("✅ Default admin created: admin@smartcampus.com / admin123");
            }

            // Create sample events if none exist
            if (eventRepo.count() == 0) {
                List<Event> events = new ArrayList<>();

                Event e1 = Event.builder()
                        .title("AI & Machine Learning Summit 2026")
                        .description("Explore the latest breakthroughs in artificial intelligence and machine learning. Featuring keynote speakers from top tech companies, hands-on workshops, and networking opportunities.")
                        .eventDate(LocalDate.now().plusDays(7))
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(17, 0))
                        .venue("Main Auditorium")
                        .venueAddress("Building A, Smart Campus University")
                        .latitude(17.385044)
                        .longitude(78.486671)
                        .category("Technology")
                        .department("Computer Science")
                        .organizer("CS Department")
                        .totalSeats(100)
                        .availableSeats(100)
                        .seatRows(10)
                        .seatColumns(10)
                        .status(Event.EventStatus.UPCOMING)
                        .registrationDeadline(LocalDateTime.now().plusDays(5))
                        .build();
                events.add(e1);

                Event e2 = Event.builder()
                        .title("Annual Cultural Fest - Rhythms 2026")
                        .description("The biggest cultural extravaganza of the year! Music performances, dance competitions, drama, and art exhibitions. Join us for two days of pure entertainment.")
                        .eventDate(LocalDate.now().plusDays(14))
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(20, 0))
                        .venue("Open Air Theatre")
                        .venueAddress("Central Ground, Smart Campus University")
                        .latitude(17.386044)
                        .longitude(78.487671)
                        .category("Cultural")
                        .department("All Departments")
                        .organizer("Cultural Committee")
                        .totalSeats(200)
                        .availableSeats(200)
                        .seatRows(10)
                        .seatColumns(20)
                        .status(Event.EventStatus.UPCOMING)
                        .registrationDeadline(LocalDateTime.now().plusDays(12))
                        .build();
                events.add(e2);

                Event e3 = Event.builder()
                        .title("Hackathon 2026 - Code for Change")
                        .description("24-hour hackathon focusing on sustainable technology solutions. Form teams, build prototypes, and win exciting prizes!")
                        .eventDate(LocalDate.now().plusDays(21))
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(8, 0))
                        .venue("Innovation Lab")
                        .venueAddress("Tech Park, Smart Campus University")
                        .latitude(17.384044)
                        .longitude(78.485671)
                        .category("Technology")
                        .department("Computer Science")
                        .organizer("Tech Club")
                        .totalSeats(50)
                        .availableSeats(50)
                        .seatRows(5)
                        .seatColumns(10)
                        .status(Event.EventStatus.UPCOMING)
                        .registrationDeadline(LocalDateTime.now().plusDays(19))
                        .build();
                events.add(e3);

                Event e4 = Event.builder()
                        .title("Sports Meet 2026")
                        .description("Annual inter-department sports competition. Track & field events, cricket, basketball, football, and more.")
                        .eventDate(LocalDate.now().plusDays(28))
                        .startTime(LocalTime.of(7, 0))
                        .endTime(LocalTime.of(18, 0))
                        .venue("University Stadium")
                        .venueAddress("Sports Complex, Smart Campus University")
                        .latitude(17.387044)
                        .longitude(78.488671)
                        .category("Sports")
                        .department("All Departments")
                        .organizer("Sports Committee")
                        .totalSeats(150)
                        .availableSeats(150)
                        .seatRows(10)
                        .seatColumns(15)
                        .status(Event.EventStatus.UPCOMING)
                        .registrationDeadline(LocalDateTime.now().plusDays(26))
                        .build();
                events.add(e4);

                Event e5 = Event.builder()
                        .title("Guest Lecture: Future of Quantum Computing")
                        .description("Distinguished lecture by Dr. Sarah Chen on quantum computing advances and their impact on cryptography and drug discovery.")
                        .eventDate(LocalDate.now().plusDays(3))
                        .startTime(LocalTime.of(14, 0))
                        .endTime(LocalTime.of(16, 0))
                        .venue("Seminar Hall B")
                        .venueAddress("Academic Block, Smart Campus University")
                        .latitude(17.385544)
                        .longitude(78.486171)
                        .category("Academic")
                        .department("Physics")
                        .organizer("Physics Department")
                        .totalSeats(80)
                        .availableSeats(80)
                        .seatRows(8)
                        .seatColumns(10)
                        .status(Event.EventStatus.UPCOMING)
                        .registrationDeadline(LocalDateTime.now().plusDays(2))
                        .build();
                events.add(e5);

                Event e6 = Event.builder()
                        .title("Workshop: Cloud & DevOps Essentials")
                        .description("Hands-on workshop covering AWS, Docker, Kubernetes, and CI/CD pipelines. Bring your laptops!")
                        .eventDate(LocalDate.now().plusDays(10))
                        .startTime(LocalTime.of(9, 30))
                        .endTime(LocalTime.of(16, 30))
                        .venue("Computer Lab 3")
                        .venueAddress("IT Building, Smart Campus University")
                        .latitude(17.385244)
                        .longitude(78.486871)
                        .category("Workshop")
                        .department("Computer Science")
                        .organizer("Cloud Computing Club")
                        .totalSeats(60)
                        .availableSeats(60)
                        .seatRows(6)
                        .seatColumns(10)
                        .status(Event.EventStatus.UPCOMING)
                        .registrationDeadline(LocalDateTime.now().plusDays(8))
                        .build();
                events.add(e6);

                List<Event> savedEvents = eventRepo.saveAll(events);

                // Generate seats for each event
                for (Event event : savedEvents) {
                    List<Seat> seats = new ArrayList<>();
                    for (int r = 1; r <= event.getSeatRows(); r++) {
                        for (int c = 1; c <= event.getSeatColumns(); c++) {
                            String label = (char) ('A' + r - 1) + String.valueOf(c);
                            Seat seat = Seat.builder()
                                    .event(event)
                                    .seatLabel(label)
                                    .seatRow(r)
                                    .seatCol(c)
                                    .status(Seat.SeatStatus.AVAILABLE)
                                    .seatType(r <= 2 ? "VIP" : "Regular")
                                    .build();
                            seats.add(seat);
                        }
                    }
                    seatRepo.saveAll(seats);
                }

                System.out.println("✅ Sample events and seats created successfully!");
            }
        };
    }
}
