package org.example.prspatientregistrationsystem.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    JwtService service;
    static class SimpleUser implements UserDetails {
        private final String username;
        SimpleUser(String username) { this.username = username; }
        @Override public Collection<? extends GrantedAuthority> getAuthorities() { return Collections.emptyList(); }
        @Override public String getPassword() { return "pass"; }
        @Override public String getUsername() { return username; }
        @Override public boolean isAccountNonExpired() { return true; }
        @Override public boolean isAccountNonLocked() { return true; }
        @Override public boolean isCredentialsNonExpired() { return true; }
        @Override public boolean isEnabled() { return true; }
    }

    @BeforeEach
    void setUp() {
        service = new JwtService();
        service.secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        service.jwtExpiration = 86400000L;
    }

    @Test void testGenerateToken() {
        UserDetails user = new SimpleUser("user");
        assertNotNull(service.generateToken(user));
    }
    @Test void testValidateToken() {
        UserDetails user = new SimpleUser("user");
        String token = service.generateToken(user);
        assertTrue(service.isTokenValid(token, user));
    }
    @Test void testExtractUsername() {
        UserDetails user = new SimpleUser("user");
        String token = service.generateToken(user);
        assertEquals("user", service.extractUsername(token));
    }
} 