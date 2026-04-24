package com.smartcampus.service;

import com.smartcampus.entity.Certificate;
import com.smartcampus.entity.Registration;
import com.smartcampus.repository.CertificateRepository;
import com.smartcampus.repository.RegistrationRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final RegistrationRepository registrationRepository;

    @Value("${app.certificate.path:./certificates/}")
    private String certificatePath;

    public byte[] generateCertificate(Long studentId, Long eventId) throws Exception {
        Registration reg = registrationRepository.findByStudentIdAndEventId(studentId, eventId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!reg.isAttended()) {
            throw new RuntimeException("Student has not attended this event");
        }

        Path directory = Path.of(certificatePath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String certCode = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(PageSize.A4.rotate());
        Document document = new Document(pdf);

        DeviceRgb primaryColor = new DeviceRgb(102, 126, 234);
        DeviceRgb goldColor = new DeviceRgb(212, 175, 55);

        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("CERTIFICATE OF PARTICIPATION")
                .setFontSize(28).setBold().setFontColor(primaryColor)
                .setTextAlignment(TextAlignment.CENTER));

        SolidLine line = new SolidLine(2f);
        line.setColor(goldColor);
        document.add(new LineSeparator(line));

        document.add(new Paragraph("\nThis is to certify that\n")
                .setFontSize(14).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(reg.getStudent().getFullName())
                .setFontSize(24).setBold().setFontColor(primaryColor)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\n(Student ID: " + reg.getStudent().getStudentId() + ")")
                .setFontSize(12).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\nhas successfully participated in\n")
                .setFontSize(14).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(reg.getEvent().getTitle())
                .setFontSize(20).setBold().setFontColor(new DeviceRgb(118, 75, 162))
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\nheld on " +
                reg.getEvent().getEventDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) +
                " at " + reg.getEvent().getVenue())
                .setFontSize(12).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\n\n"));
        document.add(new LineSeparator(line));

        document.add(new Paragraph("\nCertificate Code: " + certCode +
                "    |    Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .setFontSize(10).setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Smart Campus Event Management System")
                .setFontSize(10).setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER));

        document.close();

        byte[] pdfBytes = baos.toByteArray();
        String filePath = certificatePath + certCode + ".pdf";
        Files.write(Path.of(filePath), pdfBytes);

        Certificate cert = Certificate.builder()
                .student(reg.getStudent())
                .event(reg.getEvent())
                .certificateCode(certCode)
                .filePath(filePath)
                .build();
        certificateRepository.save(cert);

        reg.setCertificateGenerated(true);
        reg.setCertificatePath(filePath);
        registrationRepository.save(reg);

        return pdfBytes;
    }

    public List<Certificate> findByStudentId(Long studentId) {
        return certificateRepository.findByStudentId(studentId);
    }

    public Optional<Certificate> findByStudentAndEvent(Long studentId, Long eventId) {
        return certificateRepository.findByStudentIdAndEventId(studentId, eventId);
    }
}
