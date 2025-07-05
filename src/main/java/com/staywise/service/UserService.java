package com.staywise.service;

import com.staywise.dto.UserDto;
import com.staywise.dto.UserResponseDto;
import com.staywise.mapper.UserMapper;
import com.staywise.model.EmailConfirmationToken;
import com.staywise.model.User;
import com.staywise.repository.EmailTokenRepository;
import com.staywise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username já existe");
        }

        if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(dto.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já existe");
        }

        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(false);
        user = userRepository.save(user);

        EmailConfirmationToken token = new EmailConfirmationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        emailTokenRepository.save(token);

        String link = baseUrl + "auth/confirm?token=" + token.getToken();
        String message = "Olá, confirma o teu email clicando neste link: " + link;
        emailService.sendEmail(user.getEmail(), "Confirmação de Email - Staywise", message);

        return userMapper.toDto(user);
    }
}
