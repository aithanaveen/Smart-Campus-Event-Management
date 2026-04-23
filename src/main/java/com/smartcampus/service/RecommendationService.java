package com.smartcampus.service;

import com.smartcampus.entity.Event;
import com.smartcampus.repository.EventRepository;
import com.smartcampus.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    public List<Event> getRecommendations(Long studentId, String department, String interests) {
        List<Event> upcoming = eventRepository.findUpcomingEvents(LocalDate.now());
        List<Object[]> topCategories = registrationRepository.findTopCategoriesByStudent(studentId);

        Set<String> preferredCategories = new HashSet<>();
        for (Object[] row : topCategories) {
            preferredCategories.add((String) row[0]);
        }

        if (interests != null && !interests.isEmpty()) {
            String[] interestArray = interests.split(",");
            for (String interest : interestArray) {
                preferredCategories.add(interest.trim());
            }
        }

        List<Event> scored = new ArrayList<>(upcoming);
        scored.sort((a, b) -> {
            int scoreA = 0, scoreB = 0;
            if (preferredCategories.contains(a.getCategory())) scoreA += 3;
            if (preferredCategories.contains(b.getCategory())) scoreB += 3;
            if (department != null && department.equals(a.getDepartment())) scoreA += 2;
            if (department != null && department.equals(b.getDepartment())) scoreB += 2;
            if (a.getAvailableSeats() > 0) scoreA += 1;
            if (b.getAvailableSeats() > 0) scoreB += 1;
            return scoreB - scoreA;
        });

        return scored.stream().limit(6).collect(Collectors.toList());
    }
}
