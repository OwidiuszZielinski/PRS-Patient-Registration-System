package org.example.prspatientregistrationsystem.core.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

class AppUserTest {
    @Test void testBuilderAndGetters() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("pass");
        user.setRole(UserRole.DOCTOR);
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertEquals(UserRole.DOCTOR, user.getRole());
    }
    @Test void testAuthorities() {
        AppUser user = new AppUser();
        user.setRole(UserRole.ADMIN);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_ADMIN", authorities.iterator().next().getAuthority());
    }
    @Test void testAccountStatus() {
        AppUser user = new AppUser();
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
    @Test void testEqualsAndHashCode() {
        AppUser user1 = new AppUser();
        user1.setId(1L);
        AppUser user2 = new AppUser();
        user2.setId(1L);
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
} 