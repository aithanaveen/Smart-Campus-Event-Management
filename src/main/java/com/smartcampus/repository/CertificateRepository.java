package com.smartcampus.repository;

import com.smartcampus.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByStudentId(Long studentId);
    Optional<Certificate> findByStudentIdAndEventId(Long studentId, Long eventId);
    Optional<Certificate> findByCertificateCode(String certificateCode);
    boolean existsByStudentIdAndEventId(Long studentId, Long eventId);
}
