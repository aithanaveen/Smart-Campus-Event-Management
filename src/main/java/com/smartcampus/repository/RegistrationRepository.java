package com.smartcampus.repository;

import com.smartcampus.entity.Registration;
import com.smartcampus.entity.Registration.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByStudentId(Long studentId);
    List<Registration> findByEventId(Long eventId);
    Optional<Registration> findByStudentIdAndEventId(Long studentId, Long eventId);
    Optional<Registration> findByRegistrationCode(String registrationCode);
    boolean existsByStudentIdAndEventId(Long studentId, Long eventId);
    long countByEventId(Long eventId);
    long countByEventIdAndStatus(Long eventId, RegistrationStatus status);
    long countByEventIdAndAttended(Long eventId, boolean attended);

    @Query("SELECT r FROM Registration r WHERE r.student.id = :studentId AND r.status = 'CONFIRMED'")
    List<Registration> findConfirmedByStudent(@Param("studentId") Long studentId);

    @Query("SELECT r.event.category, COUNT(r) FROM Registration r WHERE r.student.id = :studentId GROUP BY r.event.category ORDER BY COUNT(r) DESC")
    List<Object[]> findTopCategoriesByStudent(@Param("studentId") Long studentId);
}
