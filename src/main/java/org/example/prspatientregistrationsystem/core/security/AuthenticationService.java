package org.example.prspatientregistrationsystem.core.security;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.patient.PatientService;
import org.example.prspatientregistrationsystem.core.patient.dto.PatientDto;
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
        if (appUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (appUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        var appUser = buildAppUser(request);
        var savedUser = appUserRepository.save(appUser);

        var patientDto = buildPatient(request, savedUser);
        patientService.save(patientDto);

        final String jwt = jwtService.generateToken(savedUser);
        return buildAuthenticationResponse(jwt, savedUser);
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

        var appUser = appUserRepository.findByUsername(request.getUsername()).orElse(null);
        var role = appUser != null ? appUser.getRole().name() : "USER";
        var email = appUser != null ? appUser.getEmail() : null;

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

    private AppUser buildAppUser(RegisterRequest request) {
        var appUser = new AppUser();
        appUser.setUsername(request.getUsername());
        appUser.setEmail(request.getEmail());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setRole(UserRole.PATIENT);
        return appUser;
    }

    private static AuthenticationResponse buildAuthenticationResponse(String jwt, AppUser savedUser) {
        return AuthenticationResponse.builder()
                .token(jwt)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    private static PatientDto buildPatient(RegisterRequest request, AppUser savedUser) {
        return PatientDto.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .identificationNumber(request.getIdentificationNumber())
                .birthDate(request.getBirthDate() != null ? LocalDate.parse(request.getBirthDate()) : null)
                .appUser(savedUser)
                .build();
    }
} 