package org.example.prspatientregistrationsystem.core.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppUserServiceTest {

    private AppUserRepository appUserRepository;
    private AppUserService appUserService;

    @BeforeEach
    void setUp() {
        appUserRepository = mock(AppUserRepository.class);
        appUserService = new AppUserService(appUserRepository);
    }

    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        String username = "john_doe";
        String password = "secure_password";

        AppUser mockUser = mock(AppUser.class);
        when(mockUser.getUsername()).thenReturn(username);
        when(mockUser.getPassword()).thenReturn(password);

        when(appUserRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = appUserService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        verify(appUserRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        String username = "unknown_user";

        when(appUserRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> appUserService.loadUserByUsername(username));

        assertEquals(username, exception.getMessage());
        verify(appUserRepository, times(1)).findByUsername(username);
    }
}