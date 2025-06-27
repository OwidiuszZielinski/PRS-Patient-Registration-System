package org.example.prspatientregistrationsystem.core.security.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationResponseTest {
    @Test void testBuilderAndGetters() {
        AuthenticationResponse resp = AuthenticationResponse.builder()
                .token("t")
                .username("u")
                .email("e")
                .role("r")
                .build();
        assertEquals("t", resp.getToken());
        assertEquals("u", resp.getUsername());
        assertEquals("e", resp.getEmail());
        assertEquals("r", resp.getRole());
    }
    @Test void testEqualsAndHashCode() {
        AuthenticationResponse r1 = AuthenticationResponse.builder().token("a").build();
        AuthenticationResponse r2 = AuthenticationResponse.builder().token("a").build();
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
    @Test void testToString() {
        AuthenticationResponse resp = AuthenticationResponse.builder().token("x").build();
        assertTrue(resp.toString().contains("x"));
    }
} 