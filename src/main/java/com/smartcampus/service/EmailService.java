package com.smartcampus.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final long OTP_VALIDITY_SECONDS = 10 * 60;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, Instant> verifiedEmails = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public OtpDispatchResult generateOTP(String email) {
        if (!isMailConfigured()) {
            return new OtpDispatchResult(false, "Mail server is not configured.");
        }

        String normalizedEmail = normalizeEmail(email);
        String otp = String.format("%06d", random.nextInt(1_000_000));
        otpStorage.put(normalizedEmail, new OtpEntry(otp, Instant.now().plusSeconds(OTP_VALIDITY_SECONDS)));
        verifiedEmails.remove(normalizedEmail);

        boolean sent = sendOTPEmail(email, otp);
        if (sent) {
            return new OtpDispatchResult(true, "OTP sent successfully");
        }

        otpStorage.remove(normalizedEmail);
        return new OtpDispatchResult(false, "Failed to deliver OTP email");
    }

    public boolean verifyOTP(String email, String otp) {
        String normalizedEmail = normalizeEmail(email);
        OtpEntry storedOTP = otpStorage.get(normalizedEmail);
        if (storedOTP == null || storedOTP.expiresAt().isBefore(Instant.now())) {
            otpStorage.remove(normalizedEmail);
            return false;
        }

        if (storedOTP.code().equals(otp)) {
            otpStorage.remove(normalizedEmail);
            verifiedEmails.put(normalizedEmail, Instant.now().plusSeconds(OTP_VALIDITY_SECONDS));
            return true;
        }

        return false;
    }

    public boolean hasOtpVerification(String email) {
        String normalizedEmail = normalizeEmail(email);
        Instant verifiedUntil = verifiedEmails.get(normalizedEmail);
        if (verifiedUntil == null || verifiedUntil.isBefore(Instant.now())) {
            verifiedEmails.remove(normalizedEmail);
            return false;
        }

        return true;
    }

    public void consumeOtpVerification(String email) {
        String normalizedEmail = normalizeEmail(email);
        verifiedEmails.remove(normalizedEmail);
    }

    public boolean sendOTPEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(mailUsername);
            helper.setSubject("Smart Campus - OTP Verification");
            helper.setText(buildOTPEmailTemplate(otp), true);

            mailSender.send(message);
            return true;
        } catch (MessagingException | RuntimeException e) {
            System.err.println("Email sending failed: " + e.getMessage());
            return false;
        }
    }

    @Async
    public void sendRegistrationConfirmation(String to, String studentName, String eventTitle,
                                             String seatNumber, String registrationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(mailUsername);
            helper.setSubject("Registration Confirmed - " + eventTitle);
            helper.setText(buildRegistrationEmailTemplate(studentName, eventTitle, seatNumber, registrationCode), true);

            mailSender.send(message);
        } catch (MessagingException | RuntimeException e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }

    @Async
    public void sendEventReminder(String to, String studentName, String eventTitle, String eventDate) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(mailUsername);
            helper.setSubject("Reminder: " + eventTitle + " is tomorrow!");
            helper.setText(buildReminderEmailTemplate(studentName, eventTitle, eventDate), true);

            mailSender.send(message);
        } catch (MessagingException | RuntimeException e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }

    @Async
    public void sendCertificateNotification(String to, String studentName, String eventTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(mailUsername);
            helper.setSubject("Certificate Available - " + eventTitle);
            helper.setText(buildCertificateEmailTemplate(studentName, eventTitle), true);

            mailSender.send(message);
        } catch (MessagingException | RuntimeException e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }

    private String buildOTPEmailTemplate(String otp) {
        return """
            <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; border-radius: 10px 10px 0 0; text-align: center;">
                    <h1 style="color: white; margin: 0;">Smart Campus</h1>
                    <p style="color: rgba(255,255,255,0.9); margin-top: 5px;">Event Management System</p>
                </div>
                <div style="background: #ffffff; padding: 30px; border: 1px solid #e0e0e0; border-radius: 0 0 10px 10px;">
                    <h2 style="color: #333;">OTP Verification</h2>
                    <p style="color: #666;">Your one-time password for event registration is:</p>
                    <div style="background: #f8f9fa; border: 2px dashed #667eea; border-radius: 10px; padding: 20px; text-align: center; margin: 20px 0;">
                        <span style="font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #667eea;">%s</span>
                    </div>
                    <p style="color: #999; font-size: 14px;">This OTP is valid for 10 minutes. Do not share it with anyone.</p>
                </div>
            </div>
            """.formatted(otp);
    }

    private String buildRegistrationEmailTemplate(String name, String event, String seat, String code) {
        return """
            <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; border-radius: 10px 10px 0 0; text-align: center;">
                    <h1 style="color: white; margin: 0;">Smart Campus</h1>
                </div>
                <div style="background: #ffffff; padding: 30px; border: 1px solid #e0e0e0; border-radius: 0 0 10px 10px;">
                    <h2 style="color: #28a745;">Registration Confirmed!</h2>
                    <p>Dear <strong>%s</strong>,</p>
                    <p>You have successfully registered for:</p>
                    <div style="background: #f8f9fa; border-left: 4px solid #667eea; padding: 15px; margin: 15px 0; border-radius: 5px;">
                        <strong>Event:</strong> %s<br>
                        <strong>Seat:</strong> %s<br>
                        <strong>Registration Code:</strong> %s
                    </div>
                    <p>Your QR event pass is available in your dashboard. Please present it at the event entrance.</p>
                </div>
            </div>
            """.formatted(name, event, seat, code);
    }

    private String buildReminderEmailTemplate(String name, String event, String date) {
        return """
            <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); padding: 30px; border-radius: 10px 10px 0 0; text-align: center;">
                    <h1 style="color: white; margin: 0;">Event Reminder</h1>
                </div>
                <div style="background: #ffffff; padding: 30px; border: 1px solid #e0e0e0; border-radius: 0 0 10px 10px;">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>This is a friendly reminder that <strong>%s</strong> is scheduled for <strong>%s</strong>.</p>
                    <p>Don't forget to bring your QR event pass!</p>
                </div>
            </div>
            """.formatted(name, event, date);
    }

    private String buildCertificateEmailTemplate(String name, String event) {
        return """
            <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); padding: 30px; border-radius: 10px 10px 0 0; text-align: center;">
                    <h1 style="color: white; margin: 0;">Certificate Available</h1>
                </div>
                <div style="background: #ffffff; padding: 30px; border: 1px solid #e0e0e0; border-radius: 0 0 10px 10px;">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your participation certificate for <strong>%s</strong> is now available for download.</p>
                    <p>Login to your dashboard to download it.</p>
                </div>
            </div>
            """.formatted(name, event);
    }

    private boolean isMailConfigured() {
        return StringUtils.hasText(mailUsername) && StringUtils.hasText(mailPassword);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private record OtpEntry(String code, Instant expiresAt) {
    }

    public record OtpDispatchResult(boolean delivered, String message) {
    }
}
