package com.domiledge.controller;

import com.domiledge.dto.AuthRequestDto;
import com.domiledge.dto.AuthResponseDto;
import com.domiledge.model.User;
import com.domiledge.repository.UserRepository;
import com.domiledge.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequestDto request) {
        User user;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow();
        } catch (AuthenticationException | NoSuchElementException exception) {
            log.error("[ERROR] {} - Authentication Failed for {}", LocalDateTime.now(), exception);
            return ResponseEntity.badRequest().body(Map.of("error", "Authentication Failed"));
        }

        String token = jwtService.generateToken(user);
        log.info("[LOGIN] {} logged in at {}", user.getUsername(), LocalDateTime.now());

        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal User user) {
        log.info("[LOGOUT] {} logged out at {}", user.getUsername(), LocalDateTime.now());
        return ResponseEntity.ok("Logout successful");
    }
}