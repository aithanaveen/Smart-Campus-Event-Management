-- ============================================
-- SMART CAMPUS EVENT MANAGEMENT SYSTEM
-- MySQL Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS smart_campus_db;
USE smart_campus_db;

-- Students Table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    interests VARCHAR(500),
    profile_image VARCHAR(500),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_STUDENT',
    created_at DATETIME,
    updated_at DATETIME,
    INDEX idx_student_email (email),
    INDEX idx_student_dept (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Admins Table
CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_ADMIN',
    department VARCHAR(100),
    created_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Events Table
CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    venue VARCHAR(255) NOT NULL,
    venue_address VARCHAR(500),
    latitude DOUBLE,
    longitude DOUBLE,
    category VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    organizer VARCHAR(255),
    image_url VARCHAR(500),
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    seat_rows INT,
    seat_columns INT,
    status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'UPCOMING',
    registration_deadline DATETIME,
    created_at DATETIME,
    updated_at DATETIME,
    INDEX idx_event_status (status),
    INDEX idx_event_date (event_date),
    INDEX idx_event_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Registrations Table
CREATE TABLE IF NOT EXISTS registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    seat_number VARCHAR(20),
    qr_code_path VARCHAR(500),
    registration_code VARCHAR(50) UNIQUE,
    status ENUM('CONFIRMED', 'CANCELLED', 'WAITLISTED') NOT NULL DEFAULT 'CONFIRMED',
    attended BOOLEAN NOT NULL DEFAULT FALSE,
    certificate_generated BOOLEAN NOT NULL DEFAULT FALSE,
    certificate_path VARCHAR(500),
    registered_at DATETIME,
    attended_at DATETIME,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_event (student_id, event_id),
    INDEX idx_reg_code (registration_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seats Table
CREATE TABLE IF NOT EXISTS seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    seat_label VARCHAR(20) NOT NULL,
    seat_row INT NOT NULL,
    seat_col INT NOT NULL,
    status ENUM('AVAILABLE', 'BOOKED', 'BLOCKED') NOT NULL DEFAULT 'AVAILABLE',
    booked_by BIGINT,
    seat_type VARCHAR(50),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (booked_by) REFERENCES students(id) ON DELETE SET NULL,
    UNIQUE KEY uk_event_seat (event_id, seat_label),
    INDEX idx_seat_status (event_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Attendance Table
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    registration_id BIGINT,
    checked_in_at DATETIME,
    verification_method VARCHAR(50),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (registration_id) REFERENCES registrations(id) ON DELETE SET NULL,
    UNIQUE KEY uk_attendance (student_id, event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Feedback Table
CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,
    submitted_at DATETIME,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    UNIQUE KEY uk_feedback (student_id, event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Certificates Table
CREATE TABLE IF NOT EXISTS certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    certificate_code VARCHAR(50) UNIQUE,
    file_path VARCHAR(500),
    generated_at DATETIME,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_email VARCHAR(255),
    subject VARCHAR(255),
    message TEXT,
    type ENUM('OTP', 'REGISTRATION_CONFIRMATION', 'EVENT_REMINDER', 'EVENT_CANCELLATION', 'CERTIFICATE_AVAILABLE'),
    sent BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at DATETIME,
    created_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- SAMPLE DATA (Optional - loaded by DataInitializer.java)
-- ============================================
-- Admin and sample events are auto-created by the application on first run
-- Default admin: admin@smartcampus.com / admin123
