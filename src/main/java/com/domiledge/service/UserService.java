package com.domiledge.service;

import com.domiledge.dto.UserDto;
import com.domiledge.dto.UserResponseDto;
import com.domiledge.mapper.UserMapper;
import com.domiledge.model.EmailConfirmationToken;
import com.domiledge.model.Role;
import com.domiledge.model.User;
import com.domiledge.repository.EmailTokenRepository;
import com.domiledge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

    public UserResponseDto register(UserDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.error("Username: {} already exists", dto.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exist");
        }

        String email = dto.getEmail();
        if (!StringUtils.hasText(email)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Invalid");

        if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            log.error("Email: {} already exists", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exist");
        }

        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(false);
        user.setRole(Role.USER);
        user = userRepository.save(user);

        EmailConfirmationToken token = new EmailConfirmationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        emailTokenRepository.save(token);

        String link = baseUrl + "auth/confirm?token=" + token.getToken();
        String message = "Hello, click here to confirm your email: " + link;
        emailService.sendEmail(user.getEmail(), "Email Confirmation - Domiledge", message);

        return userMapper.toDto(user);
    }

    public void confirmEmail(String token) {
        EmailConfirmationToken confirmation = emailTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido"));

        if (confirmation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirado");
        }

        User user = confirmation.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        emailTokenRepository.delete(confirmation);
    }

}
