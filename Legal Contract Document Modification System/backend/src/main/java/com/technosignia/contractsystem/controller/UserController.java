package com.technosignia.contractsystem.controller;

import com.technosignia.contractsystem.dto.RegisterRequest;
import com.technosignia.contractsystem.dto.UserDto;
import com.technosignia.contractsystem.entity.Role;
import com.technosignia.contractsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String fullName = (String) body.get("fullName");
        String email = (String) body.get("email");
        String roleStr = (String) body.get("role");
        Boolean active = (Boolean) body.get("active");
        String password = (String) body.get("password");

        Role role = roleStr != null ? Role.valueOf(roleStr) : null;

        return ResponseEntity.ok(userService.updateUser(id, fullName, email, role, active, password));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}
