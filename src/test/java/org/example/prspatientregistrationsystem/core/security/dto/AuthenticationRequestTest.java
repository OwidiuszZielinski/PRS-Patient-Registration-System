package org.example.prspatientregistrationsystem.core.security.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationRequestTest {
    @Test void testBuilderAndGetters() {
        AuthenticationRequest req = AuthenticationRequest.builder()
                .username("user")
                .password("pass")
                .build();
        assertEquals("user", req.getUsername());
        assertEquals("pass", req.getPassword());
    }
    @Test void testEqualsAndHashCode() {
        AuthenticationRequest req1 = AuthenticationRequest.builder().username("a").password("b").build();
        AuthenticationRequest req2 = AuthenticationRequest.builder().username("a").password("b").build();
        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
    }
    @Test void testToString() {
        AuthenticationRequest req = AuthenticationRequest.builder().username("x").password("y").build();
        assertTrue(req.toString().contains("x"));
    }
} 