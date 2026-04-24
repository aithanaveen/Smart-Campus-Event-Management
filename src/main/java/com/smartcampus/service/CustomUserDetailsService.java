package com.smartcampus.service;

import com.smartcampus.entity.Admin;
import com.smartcampus.entity.Student;
import com.smartcampus.repository.AdminRepository;
import com.smartcampus.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check admin first
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            Admin a = admin.get();
            return new User(a.getEmail(), a.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(a.getRole())));
        }

        // Check student
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            Student s = student.get();
            if (!s.isEnabled()) {
                throw new UsernameNotFoundException("Account is disabled");
            }
            return new User(s.getEmail(), s.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(s.getRole())));
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
