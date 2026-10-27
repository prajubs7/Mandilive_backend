package com.mandilive.mandilive.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mandilive.mandilive.auth.dto.AuthResponse;
import com.mandilive.mandilive.auth.dto.LoginRequest;
import com.mandilive.mandilive.auth.dto.RefreshRequest;
import com.mandilive.mandilive.auth.dto.RegisterRequest;
import com.mandilive.mandilive.auth.dto.UserSummary;
import com.mandilive.mandilive.common.EmailAlreadyExistsException;
import com.mandilive.mandilive.user.Role;
import com.mandilive.mandilive.user.User;
import com.mandilive.mandilive.user.UserRepository;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (req.role() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot self-register as ADMIN");
        }
        String email = normalize(req.email());
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        User user = new User();
        user.setName(req.name().trim());
        user.setEmail(email);
        user.setPhone(req.phone());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(req.role());
        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        String email = normalize(req.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, req.password()));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshRequest req) {
        try {
            String email = jwtService.extractSubject(req.refreshToken(), JwtService.TYPE_REFRESH);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
            return toResponse(user);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid refresh token");
        }
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user),
                jwtService.accessTtlSeconds(),
                new UserSummary(user.getId(), user.getName(), user.getEmail(), user.getRole()));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
