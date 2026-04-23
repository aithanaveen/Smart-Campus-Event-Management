package com.smartcampus.service;

import com.smartcampus.entity.Feedback;
import com.smartcampus.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback submitFeedback(Feedback feedback) {
        if (feedbackRepository.existsByStudentIdAndEventId(
                feedback.getStudent().getId(), feedback.getEvent().getId())) {
            throw new RuntimeException("Feedback already submitted for this event");
        }
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> findByEventId(Long eventId) {
        return feedbackRepository.findByEventId(eventId);
    }

    public Double getAverageRating(Long eventId) {
        return feedbackRepository.findAverageRatingByEventId(eventId);
    }

    public List<Object[]> getRatingDistribution(Long eventId) {
        return feedbackRepository.findRatingDistribution(eventId);
    }

    public boolean hasSubmittedFeedback(Long studentId, Long eventId) {
        return feedbackRepository.existsByStudentIdAndEventId(studentId, eventId);
    }
}
