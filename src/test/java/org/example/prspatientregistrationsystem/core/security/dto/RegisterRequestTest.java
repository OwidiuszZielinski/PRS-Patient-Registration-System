package org.example.prspatientregistrationsystem.core.security.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {
    @Test void testBuilderAndGetters() {
        RegisterRequest req = RegisterRequest.builder()
                .username("user")
                .email("e@e.com")
                .password("pass")
                .firstName("Jan")
                .lastName("Kowalski")
                .phoneNumber("123456789")
                .identificationNumber("90010112345")
                .birthDate("1990-01-01")
                .build();
        assertEquals("user", req.getUsername());
        assertEquals("e@e.com", req.getEmail());
        assertEquals("pass", req.getPassword());
        assertEquals("Jan", req.getFirstName());
        assertEquals("Kowalski", req.getLastName());
        assertEquals("123456789", req.getPhoneNumber());
        assertEquals("90010112345", req.getIdentificationNumber());
        assertEquals("1990-01-01", req.getBirthDate());
    }
    @Test void testEqualsAndHashCode() {
        RegisterRequest req1 = RegisterRequest.builder().username("a").email("b").build();
        RegisterRequest req2 = RegisterRequest.builder().username("a").email("b").build();
        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
    }
    @Test void testToString() {
        RegisterRequest req = RegisterRequest.builder().username("x").email("y").build();
        assertTrue(req.toString().contains("x"));
    }
} 