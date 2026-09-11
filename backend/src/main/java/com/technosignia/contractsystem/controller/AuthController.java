package com.technosignia.contractsystem.controller;

import com.technosignia.contractsystem.dto.AuthRequest;
import com.technosignia.contractsystem.dto.AuthResponse;
import com.technosignia.contractsystem.dto.RegisterRequest;
import com.technosignia.contractsystem.dto.UserDto;
import com.technosignia.contractsystem.entity.User;
import com.technosignia.contractsystem.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(new UserDto(user));
    }
}
