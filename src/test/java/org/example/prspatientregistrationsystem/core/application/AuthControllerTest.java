package org.example.prspatientregistrationsystem.core.application;

import org.example.prspatientregistrationsystem.core.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    private SecurityConfig securityConfig;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        securityConfig = mock(SecurityConfig.class);
        authController = new AuthController(securityConfig);
    }

    @Test
    void shouldReturnTrueWhenAuthenticated() {
        when(securityConfig.isAuthenticated()).thenReturn(true);

        boolean result = authController.isAuthenticated();

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenNotAuthenticated() {
        when(securityConfig.isAuthenticated()).thenReturn(false);

        boolean result = authController.isAuthenticated();

        assertFalse(result);
    }
}