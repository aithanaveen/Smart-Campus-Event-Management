# 🎓 AI-Powered Smart Campus Event Management System

## With Real-Time Seat Booking

A comprehensive full-stack event management system built with Spring Boot, featuring real-time seat booking via WebSockets, AI chatbot assistance, QR code event passes, email OTP verification, Google Maps integration, and PDF certificate generation.

---

## 🚀 Tech Stack

| Layer | Technologies |
|-------|-------------|
| **Backend** | Java 17, Spring Boot 3.2.5, Spring MVC, Spring Security, Spring Data JPA, Hibernate |
| **Frontend** | Thymeleaf, HTML5, CSS3, Bootstrap 5, JavaScript |
| **Database** | MySQL 8.0 |
| **Real-Time** | WebSockets (STOMP + SockJS) |
| **Email** | Java Mail Sender (Gmail SMTP) |
| **QR Code** | ZXing Library |
| **PDF** | iText 7 |
| **AI** | OpenAI API / Built-in NLP Chatbot |
| **Maps** | Google Maps API |

---

## 📋 Modules

### Student Module
- Registration & Login/Logout
- Browse, search, and filter events
- Real-time interactive seat booking
- OTP verification before registration
- QR code event pass download
- Email notifications
- Event feedback submission
- Participation certificate download

### Admin Module
- Dashboard with analytics
- Create/Update/Delete events
- Configure seating layouts
- View registrations & attendance
- QR code scanning for attendance
- View feedback reports
- Student management

### Innovation Modules
- **Real-Time Seat Booking** - WebSocket-powered seat map like movie booking
- **AI Chatbot** - Intelligent event assistant
- **QR Code System** - Unique event passes with scan-based attendance
- **OTP Verification** - Email OTP before registration
- **Event Recommendations** - Based on department, interests & history
- **Google Maps** - Venue location and directions
- **PDF Certificates** - Auto-generated participation certificates

---

## 🛠️ Setup & Installation

### Prerequisites
- Java 17+ (JDK)
- Maven 3.8+
- MySQL 8.0+
- Git

### Step 1: Clone / Navigate to Project
```bash
cd FSAD_Final_Project/smart-campus
```

### Step 2: Create MySQL Database
```sql
CREATE DATABASE smart_campus_db;
```
Or the app will auto-create it (`createDatabaseIfNotExist=true`).

### Step 3: Configure Application Properties
Edit `src/main/resources/application.properties`:

```properties
# MySQL credentials
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# Gmail SMTP (for OTP & notifications)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# API Keys (optional)
openai.api.key=your-openai-key
google.maps.api.key=your-google-maps-key
```

**Gmail App Password:** Go to Google Account → Security → 2-Step Verification → App Passwords → Generate for "Mail".

### Step 4: Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### Step 5: Access Application
- **Home:** http://localhost:8080
- **Login:** http://localhost:8080/login
- **Admin Dashboard:** http://localhost:8080/admin/dashboard
- **Student Dashboard:** http://localhost:8080/student/dashboard

### Default Admin Credentials
```
Email: admin@smartcampus.com
Password: admin123
```

---

## 📂 Project Structure

```
smart-campus/
├── pom.xml
├── src/main/java/com/smartcampus/
│   ├── SmartCampusApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java          # Spring Security
│   │   ├── WebSocketConfig.java         # WebSocket (STOMP)
│   │   └── DataInitializer.java         # Sample data loader
│   ├── entity/
│   │   ├── Student.java
│   │   ├── Admin.java
│   │   ├── Event.java
│   │   ├── Registration.java
│   │   ├── Seat.java
│   │   ├── Feedback.java
│   │   ├── Attendance.java
│   │   ├── Certificate.java
│   │   └── Notification.java
│   ├── repository/
│   │   ├── StudentRepository.java
│   │   ├── AdminRepository.java
│   │   ├── EventRepository.java
│   │   ├── RegistrationRepository.java
│   │   ├── SeatRepository.java
│   │   ├── FeedbackRepository.java
│   │   ├── AttendanceRepository.java
│   │   ├── CertificateRepository.java
│   │   └── NotificationRepository.java
│   ├── service/
│   │   ├── CustomUserDetailsService.java
│   │   ├── StudentService.java
│   │   ├── EventService.java
│   │   ├── SeatService.java
│   │   ├── RegistrationService.java
│   │   ├── EmailService.java
│   │   ├── QRCodeService.java
│   │   ├── CertificateService.java
│   │   ├── ChatbotService.java
│   │   ├── AttendanceService.java
│   │   ├── FeedbackService.java
│   │   └── RecommendationService.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── StudentController.java
│   │   ├── AdminController.java
│   │   ├── SeatApiController.java       # REST API
│   │   ├── EventApiController.java      # REST API
│   │   └── ChatbotController.java       # REST API
│   ├── dto/
│   │   ├── SeatDTO.java
│   │   ├── SeatBookingRequest.java
│   │   ├── ChatMessage.java
│   │   └── DashboardStats.java
│   └── exception/
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── application.properties
│   ├── schema.sql
│   ├── static/
│   │   ├── css/style.css
│   │   └── js/
│   │       ├── app.js
│   │       ├── seat-booking.js
│   │       └── chatbot.js
│   └── templates/
│       ├── home.html
│       ├── error.html
│       ├── fragments/layout.html
│       ├── auth/
│       │   ├── login.html
│       │   └── register.html
│       ├── student/
│       │   ├── dashboard.html
│       │   ├── events.html
│       │   ├── event-details.html
│       │   └── my-registrations.html
│       └── admin/
│           ├── dashboard.html
│           ├── events.html
│           ├── event-form.html
│           ├── registrations.html
│           ├── attendance.html
│           ├── feedback.html
│           └── students.html
```

---

## 🔌 REST API Endpoints

### Seat APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/seats/{eventId}` | Get all seats for an event |
| POST | `/api/seats/book` | Book a seat |
| PUT | `/api/seats/update` | Update seat status |

### OTP APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/otp/send` | Send OTP to email |
| POST | `/api/otp/verify` | Verify OTP |

### Event Registration
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/register-event` | Register for event |

### Chatbot
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/chatbot/message` | Send message to AI chatbot |

---

## 📊 Database Tables

| Table | Description |
|-------|-------------|
| `students` | Student accounts |
| `admins` | Admin accounts |
| `events` | Campus events |
| `registrations` | Event registrations |
| `seats` | Seating layout & booking |
| `attendance` | Event attendance records |
| `feedback` | Student feedback |
| `certificates` | Participation certificates |
| `notifications` | Email notification logs |

---

## 🔐 Spring Security

- BCrypt password encoding
- Role-based access: `ROLE_ADMIN`, `ROLE_STUDENT`
- Form-based authentication
- CSRF protection (disabled for APIs)
- Custom success handler (redirects by role)

---

## 📝 Key Spring Annotations Used

`@SpringBootApplication`, `@Controller`, `@RestController`, `@Service`, `@Repository`,
`@Autowired`, `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@OneToMany`, `@ManyToOne`,
`@ControllerAdvice`, `@ExceptionHandler`, `@Valid`, `@NotBlank`, `@Email`, `@Size`,
`@Transactional`, `@Async`, `@EnableWebSocketMessageBroker`, `@EnableScheduling`,
`@Configuration`, `@EnableWebSecurity`, `@Bean`, `@Value`, `@PrePersist`

---

## 👨‍💻 Author

Smart Campus Event Management System - End Semester Review Project

---

## 📄 License

This project is developed for academic purposes.
