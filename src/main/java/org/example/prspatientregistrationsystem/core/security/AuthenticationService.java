package org.example.prspatientregistrationsystem.core.security;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.patient.Patient;
import org.example.prspatientregistrationsystem.core.patient.PatientRepository;
import org.example.prspatientregistrationsystem.core.patient.dto.PatientDto;
import org.example.prspatientregistrationsystem.core.patient.PatientService;
import org.example.prspatientregistrationsystem.core.security.dto.AuthenticationRequest;
import org.example.prspatientregistrationsystem.core.security.dto.AuthenticationResponse;
import org.example.prspatientregistrationsystem.core.security.dto.RegisterRequest;
import org.example.prspatientregistrationsystem.core.user.AppUser;
import org.example.prspatientregistrationsystem.core.user.AppUserRepository;
import org.example.prspatientregistrationsystem.core.user.UserRole;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientService patientService;

    public AuthenticationResponse register(RegisterRequest request) {
        // Check if user already exists
        if (appUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        if (appUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());
        appUser.setEmail(request.getEmail());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setRole(UserRole.PATIENT);
        
        AppUser savedUser = appUserRepository.save(appUser);

        // Create patient
        var patientDto = PatientDto.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .identificationNumber(request.getIdentificationNumber())
                .birthDate(request.getBirthDate() != null ? LocalDate.parse(request.getBirthDate()) : null)
                .appUser(savedUser)
                .build();
        
        patientService.save(patientDto);

        // Generate token
        final String jwt = jwtService.generateToken(savedUser);
        
        return AuthenticationResponse.builder()
                .token(jwt)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        final UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtService.generateToken(user);
        
        AppUser appUser = appUserRepository.findByUsername(request.getUsername()).orElse(null);
        String role = appUser != null ? appUser.getRole().name() : "USER";
        String email = appUser != null ? appUser.getEmail() : null;
        
        return AuthenticationResponse.builder()
                .token(jwt)
                .username(request.getUsername())
                .email(email)
                .role(role)
                .build();
    }

    public boolean validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                return jwtService.isTokenValid(token, userDetails);
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
} 