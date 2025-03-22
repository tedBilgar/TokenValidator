package ru.tedbilgar.tokenvalidator.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/validate")
public class TokenValidationController {

    @GetMapping
    public Map<String, Object> validateToken(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "valid", true,
                "subject", jwt.getSubject(),
                "claims", jwt.getClaims()
        );
    }
}
