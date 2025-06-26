package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.patient.PatientService;
import org.example.prspatientregistrationsystem.core.patient.dto.PatientDto;
import org.example.prspatientregistrationsystem.core.security.AuthenticationService;
import org.example.prspatientregistrationsystem.core.security.dto.AuthenticationRequest;
import org.example.prspatientregistrationsystem.core.security.dto.AuthenticationResponse;
import org.example.prspatientregistrationsystem.core.security.dto.RegisterRequest;
import org.example.prspatientregistrationsystem.core.user.AppUser;
import org.example.prspatientregistrationsystem.core.user.AppUserRepository;
import org.example.prspatientregistrationsystem.core.user.UserRole;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientService patientService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            return ResponseEntity.ok(authenticationService.validateToken(jwt));
        }
        return ResponseEntity.ok(false);
    }

    @GetMapping("/oauth2/google")
    public ResponseEntity<String> initiateGoogleOAuth2() {
        return ResponseEntity.ok("Redirect to: /oauth2/authorization/google");
    }
}
