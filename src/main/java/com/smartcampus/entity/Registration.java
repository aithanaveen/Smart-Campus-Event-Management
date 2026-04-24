package com.smartcampus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private String seatNumber;

    private String qrCodePath;

    @Column(unique = true)
    private String registrationCode;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.CONFIRMED;

    @Builder.Default
    private boolean attended = false;

    @Builder.Default
    private boolean certificateGenerated = false;

    private String certificatePath;

    private LocalDateTime registeredAt;

    private LocalDateTime attendedAt;

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }

    public enum RegistrationStatus {
        CONFIRMED, CANCELLED, WAITLISTED
    }
}
