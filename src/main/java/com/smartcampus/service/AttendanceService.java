package com.smartcampus.service;

import com.smartcampus.entity.Attendance;
import com.smartcampus.entity.Registration;
import com.smartcampus.repository.AttendanceRepository;
import com.smartcampus.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final RegistrationRepository registrationRepository;

    @Transactional
    public Attendance markAttendance(String registrationCode) {
        Registration reg = registrationRepository.findByRegistrationCode(registrationCode)
                .orElseThrow(() -> new RuntimeException("Invalid registration code"));

        if (attendanceRepository.existsByStudentIdAndEventId(
                reg.getStudent().getId(), reg.getEvent().getId())) {
            throw new RuntimeException("Attendance already marked");
        }

        reg.setAttended(true);
        reg.setAttendedAt(LocalDateTime.now());
        registrationRepository.save(reg);

        Attendance attendance = Attendance.builder()
                .student(reg.getStudent())
                .event(reg.getEvent())
                .registration(reg)
                .verificationMethod("QR_SCAN")
                .build();
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> findByEventId(Long eventId) {
        return attendanceRepository.findByEventId(eventId);
    }

    public long countByEventId(Long eventId) {
        return attendanceRepository.countByEventId(eventId);
    }

    public long count() {
        return attendanceRepository.count();
    }
}
