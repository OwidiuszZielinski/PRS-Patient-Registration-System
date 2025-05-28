package org.example.prspatientregistrationsystem.core.application;

import org.example.prspatientregistrationsystem.core.mail.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;


    @Test
    void sendEmail_ShouldReturnServiceResponse() {
        String to = "ordinary.patient@example.com";
        String subject = "Complaint";
        String body = "How to schedule NFZ visit sooner than in 5 years?";

        String expectedResponse = "Email sent successfully!";

        when(emailService.sendEmail(to, subject, body)).thenReturn(expectedResponse);

        String actualResponse = emailController.sendEmail(to, subject, body);

        assertEquals(expectedResponse, actualResponse);

        verify(emailService, times(1)).sendEmail(to, subject, body);
    }
}