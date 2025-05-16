package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.mail.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email/")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping()
    public String sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body
    ) {
        return emailService.sendEmail(to, subject, body);
    }
}
