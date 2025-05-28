package org.example.prspatientregistrationsystem.core.mail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void testSendEmail_success() {
        String to = "ordinary.patient@example.com";
        String subject = "Complaint";
        String body = "How to schedule NFZ visit sooner than in 5 years?";

        String result = emailService.sendEmail(to, subject, body);

        assertEquals("Email sent successfully!", result);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertArrayEquals(new String[]{to}, sentMessage.getTo());
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }

    @Test
    public void testSendEmail_failure() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        String result = emailService.sendEmail("x@example.com", "Subject", "Body");

        assertTrue(result.contains("Error while sending email: SMTP error"));
    }
}