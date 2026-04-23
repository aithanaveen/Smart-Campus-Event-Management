package com.smartcampus.repository;

import com.smartcampus.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEventId(Long eventId);
    List<Feedback> findByStudentId(Long studentId);
    Optional<Feedback> findByStudentIdAndEventId(Long studentId, Long eventId);
    boolean existsByStudentIdAndEventId(Long studentId, Long eventId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.event.id = :eventId")
    Double findAverageRatingByEventId(@Param("eventId") Long eventId);

    @Query("SELECT f.rating, COUNT(f) FROM Feedback f WHERE f.event.id = :eventId GROUP BY f.rating")
    List<Object[]> findRatingDistribution(@Param("eventId") Long eventId);
}
