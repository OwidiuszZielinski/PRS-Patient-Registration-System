package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.user.AppUser;
import org.example.prspatientregistrationsystem.core.user.AppUserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegistrationController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register", consumes = "application/json")
    public AppUser register(@RequestBody AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return appUserRepository.save(user);
    }

}
