package ru.tedbilgar.tokenvalidator.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TokenValidationControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldValidateTokenSuccessfully() throws Exception {
        String token = "valid.jwt.token";
        Jwt jwt = new Jwt("token-value", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),  // Заголовки
                Map.of("sub", "test-user", "role", "USER") // Полезная нагрузка (claims)
        );

        when(jwtDecoder.decode(token)).thenReturn(jwt);

        mockMvc.perform(get("/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                                                    {
                                                      "valid": true,
                                                      "claims": {
                                                        "role": "USER",
                                                        "sub": "test-user"
                                                      },
                                                      "subject": "test-user"
                                                    }
                                                    """, true));
    }

    @Test
    void shouldReturnUnauthorizedForInvalidToken() throws Exception {
        String token = "invalid.jwt.token";

        when(jwtDecoder.decode(token)).thenThrow(JwtValidationException.class);

        mockMvc.perform(get("/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""))
                .andExpect(header().stringValues("WWW-Authenticate", "Bearer error=\"invalid_token\", error_uri=\"https://tools.ietf.org/html/rfc6750#section-3.1\""));
    }
}