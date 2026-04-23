package com.smartcampus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateOTP(String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        otpStorage.put(email, otp);

        // Send OTP email
        try {
            sendOTPEmail(email, otp);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }

        return otp;
    }

    public boolean verifyOTP(String email, String otp) {
        String storedOTP = otpStorage.get(email);
        if (storedOTP != null && storedOTP.equals(otp)) {
            otpStorage.remove(email);
            return true;
        }
        return false;
    }

    @Async
    public void sendOTPEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("🔐 Smart Campus - OTP Verification");
            helper.setText(buildOTPEmailTemplate(otp), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }

    @Async
    public void sendRegistrationConfirmation(String to, String studentName, String eventTitle,
                                              String seatNumber, String registrationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("✅ Registration Confirmed - " + eventTitle);
            helper.setText(buildRegistrationEmailTemplate(studentName, eventTitle, seatNumber, registrationCode), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }

    @Async
    public void sendEventReminder(String to, String studentName, String eventTitle, String eventDate) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("⏰ Reminder: " + eventTitle + " is tomorrow!");
            helper.setText(buildReminderEmailTemplate(studentName, eventTitle, eventDate), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }

    @Async
    public void sendCertificateNotification(String to, String studentName, String eventTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("🎓 Certificate Available - " + eventTitle);
            helper.setText(buildCertificateEmailTemplate(studentName, eventTitle), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }

    private String buildOTPEmailTemplate(String otp) {
        return """
            <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; border-radius: 10px 10px 0 0; text-align: center;">
                    <h1 style="color: white; margin: 0;">🎓 Smart Campus</h1>
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
                    <h1 style="color: white; margin: 0;">🎓 Smart Campus</h1>
                </div>
                <div style="background: #ffffff; padding: 30px; border: 1px solid #e0e0e0; border-radius: 0 0 10px 10px;">
                    <h2 style="color: #28a745;">✅ Registration Confirmed!</h2>
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
                    <h1 style="color: white; margin: 0;">⏰ Event Reminder</h1>
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
                    <h1 style="color: white; margin: 0;">🎓 Certificate Available</h1>
                </div>
                <div style="background: #ffffff; padding: 30px; border: 1px solid #e0e0e0; border-radius: 0 0 10px 10px;">
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Your participation certificate for <strong>%s</strong> is now available for download.</p>
                    <p>Login to your dashboard to download it.</p>
                </div>
            </div>
            """.formatted(name, event);
    }
}
