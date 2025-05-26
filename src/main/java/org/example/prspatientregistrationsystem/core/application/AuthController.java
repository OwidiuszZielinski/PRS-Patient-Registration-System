package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.security.SecurityConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final SecurityConfig securityConfig;

    @GetMapping
    public boolean isAuthenticated() {
        return securityConfig.isAuthenticated();
    }
}
