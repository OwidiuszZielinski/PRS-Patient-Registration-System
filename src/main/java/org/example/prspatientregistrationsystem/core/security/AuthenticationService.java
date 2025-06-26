package org.example.prspatientregistrationsystem.core.security;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.security.dto.AuthenticationRequest;
import org.example.prspatientregistrationsystem.core.security.dto.AuthenticationResponse;
import org.example.prspatientregistrationsystem.core.user.AppUser;
import org.example.prspatientregistrationsystem.core.user.AppUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;

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
        
        return AuthenticationResponse.builder()
                .token(jwt)
                .username(request.getUsername())
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