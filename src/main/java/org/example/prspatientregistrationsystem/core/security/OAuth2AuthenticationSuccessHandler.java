package org.example.prspatientregistrationsystem.core.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.security.dto.AuthenticationResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final OAuth2Service oAuth2Service;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        System.out.println("OAuth2AuthenticationSuccessHandler - Authentication successful");
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());
        
        // Process OAuth2 user and create/get patient
        AuthenticationResponse authResponse = oAuth2Service.processOAuth2User(oAuth2User);
        
        System.out.println("AuthenticationResponse created: " + authResponse.getUsername() + " with role: " + authResponse.getRole());
        
        // Redirect to frontend with authentication data
        var redirectUrl = String.format(
                "http://localhost:3000/oauth2-callback?token=%s&username=%s&email=%s&role=%s",
                authResponse.getToken(),
                authResponse.getUsername(),
                authResponse.getEmail(),
                authResponse.getRole()
        );
        
        System.out.println("Redirecting to: " + redirectUrl);
        
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        
        // Clear authentication attributes
        clearAuthenticationAttributes(request);
    }
} 