package org.example.prspatientregistrationsystem.core.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public String sendEmail(String to, String subject, String body) {
        try {
            var message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Error while sending email: " + e.getMessage();
        }
    }
}
