package com.smartcampus.dto;

import lombok.*;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DashboardStats {
    private long totalEvents;
    private long upcomingEvents;
    private long completedEvents;
    private long totalStudents;
    private long totalRegistrations;
    private long totalAttendance;
    private Map<String, Long> eventsByCategory;
    private Map<String, Long> registrationsByMonth;
}
