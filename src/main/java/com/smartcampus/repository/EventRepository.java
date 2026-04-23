package com.smartcampus.repository;

import com.smartcampus.entity.Event;
import com.smartcampus.entity.Event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    List<Event> findByCategory(String category);
    List<Event> findByDepartment(String department);
    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDate date);
    List<Event> findByEventDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT e FROM Event e WHERE e.status = 'UPCOMING' AND e.eventDate >= :today ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("today") LocalDate today);

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchEvents(@Param("keyword") String keyword);

    @Query("SELECT e FROM Event e WHERE e.category = :category AND e.status = 'UPCOMING' AND e.eventDate >= :today")
    List<Event> findUpcomingByCategory(@Param("category") String category, @Param("today") LocalDate today);

    @Query("SELECT e FROM Event e WHERE e.department = :department AND e.status = 'UPCOMING' AND e.eventDate >= :today")
    List<Event> findUpcomingByDepartment(@Param("department") String department, @Param("today") LocalDate today);

    @Query("SELECT DISTINCT e.category FROM Event e")
    List<String> findAllCategories();

    @Query("SELECT DISTINCT e.department FROM Event e WHERE e.department IS NOT NULL")
    List<String> findAllDepartments();

    long countByStatus(EventStatus status);
}
