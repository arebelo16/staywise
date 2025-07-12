package com.domiledge.controller;

import com.domiledge.dto.UserDto;
import com.domiledge.dto.UserResponseDto;
import com.domiledge.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto dto) {
        Pair<Optional<UserResponseDto>, String> result = userService.register(dto);

        if (result.getFirst().isPresent()) {
            return ResponseEntity.ok(result.getFirst().get());
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", result.getSecond()));
        }
    }


    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        userService.confirmEmail(token);
        return ResponseEntity.ok("Email confirmado com sucesso!");
    }
}
