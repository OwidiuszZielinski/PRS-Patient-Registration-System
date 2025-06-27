package org.example.prspatientregistrationsystem.core.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

class PasswordConfigTest {
    @Test void testPasswordEncoderBean() {
        PasswordConfig config = new PasswordConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        String raw = "password";
        String encoded = encoder.encode(raw);
        assertNotNull(encoded);
        assertTrue(encoder.matches(raw, encoded));
    }
} 