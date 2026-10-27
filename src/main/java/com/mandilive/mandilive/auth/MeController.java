package com.mandilive.mandilive.auth;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Temporary protected endpoint to verify 401 without a token. */
@RestController
public class MeController {

    @GetMapping("/api/me")
    public Map<String, Object> me(Authentication auth) {
        return Map.of("email", auth.getName(), "authorities", auth.getAuthorities().toString());
    }
}
