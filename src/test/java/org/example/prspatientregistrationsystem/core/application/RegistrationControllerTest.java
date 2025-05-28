package org.example.prspatientregistrationsystem.core.application;

import org.example.prspatientregistrationsystem.core.user.AppUser;
import org.example.prspatientregistrationsystem.core.user.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationController registrationController;

    private AppUser inputUser;
    private AppUser savedUser;

    @BeforeEach
    void setUp() {
        inputUser = new AppUser();
        inputUser.setUsername("bobtester");
        inputUser.setPassword("plainPassword");

        savedUser = new AppUser();
        savedUser.setId(1L);
        savedUser.setUsername("bobtester");
        savedUser.setPassword("encodedPassword");
    }

    @Test
    void register_ShouldEncodePasswordAndSaveUser() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        when(appUserRepository.save(any(AppUser.class))).thenReturn(savedUser);

        AppUser result = registrationController.register(inputUser);

        verify(passwordEncoder, times(1)).encode("plainPassword");

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository, times(1)).save(userCaptor.capture());
        AppUser savedArgument = userCaptor.getValue();
        assertEquals("encodedPassword", savedArgument.getPassword());
        assertEquals("bobtester", savedArgument.getUsername());

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals(savedUser.getPassword(), result.getPassword());
    }
}