package org.example.prspatientregistrationsystem.core.security;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.patient.Patient;
import org.example.prspatientregistrationsystem.core.patient.PatientRepository;
import org.example.prspatientregistrationsystem.core.patient.PatientService;
import org.example.prspatientregistrationsystem.core.patient.dto.PatientDto;
import org.example.prspatientregistrationsystem.core.security.dto.AuthenticationResponse;
import org.example.prspatientregistrationsystem.core.user.AppUser;
import org.example.prspatientregistrationsystem.core.user.AppUserRepository;
import org.example.prspatientregistrationsystem.core.user.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2Service extends DefaultOAuth2UserService {

    private final AppUserRepository appUserRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return super.loadUser(userRequest);
    }

    public AuthenticationResponse processOAuth2User(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        // Extract user information from Google OAuth2
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");
        String googleId = (String) attributes.get("sub");
        
        System.out.println("OAuth2 processing - Email: " + email + ", FirstName: " + firstName + ", LastName: " + lastName);
        
        // Generate username from email or Google ID
        String username = email != null ? email : "google_user_" + googleId;
        
        System.out.println("Generated username: " + username);
        
        // Check if user exists by email or Google ID
        Optional<AppUser> existingUser = appUserRepository.findByEmail(email);
        AppUser user;
        
        if (existingUser.isPresent()) {
            user = existingUser.get();
            System.out.println("Existing user found: " + user.getUsername());
        } else {
            // Create new user
            user = new AppUser();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("oauth2_" + googleId)); // Generate random password
            user.setRole(UserRole.PATIENT);
            user = appUserRepository.save(user);
            
            System.out.println("New user created: " + user.getUsername() + " with role: " + user.getRole());
            
            // Create patient from user data
            createPatientFromOAuth2User(user, firstName, lastName, email);
        }
        
        String token = jwtService.generateToken(user);
        
        System.out.println("Generated token for user: " + user.getUsername());
        
        return AuthenticationResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .build();
    }
    
    private void createPatientFromOAuth2User(AppUser user, String firstName, String lastName, String email) {
        // Check if patient already exists
        Optional<Patient> existingPatient = patientRepository.findByEmail(email);
        
        if (existingPatient.isEmpty()) {
            var patientDto = PatientDto.builder()
                    .firstname(firstName != null ? firstName : "Unknown")
                    .lastname(lastName != null ? lastName : "Unknown")
                    .email(email)
                    .phoneNumber("") // Will be filled later
                    .identificationNumber("") // Will be filled later
                    .birthDate(null) // Will be filled later
                    .appUser(user)
                    .build();
            
            patientService.save(patientDto);
        }
    }
} 