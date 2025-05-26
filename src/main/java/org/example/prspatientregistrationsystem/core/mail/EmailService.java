package org.example.prspatientregistrationsystem.core.mail;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public String sendEmail(String to, String subject, String body) {
        try {
            var message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Error while sending email: %s".formatted(e.getMessage());
        }
    }

    //Docelowa logika wysylka wiadomosci w dzien wizyty o 6 rano, aktualnie ustawione na minute po utworzeniu wizyty w celu sprwadzenia
    public void scheduleEmailInOneMinute(String to, String subject, String body) {
        scheduler.schedule(() -> sendEmail(to, subject, body), 1, TimeUnit.MINUTES);
        log.info("Send email to %s with before visit".formatted(to));
     }
}
