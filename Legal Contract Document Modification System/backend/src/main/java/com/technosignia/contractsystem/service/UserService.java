package com.technosignia.contractsystem.service;

import com.technosignia.contractsystem.dto.RegisterRequest;
import com.technosignia.contractsystem.dto.UserDto;
import com.technosignia.contractsystem.entity.Role;
import com.technosignia.contractsystem.entity.User;
import com.technosignia.contractsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditService auditService;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return new UserDto(user);
    }

    public UserDto createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already taken");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getRole()
        );

        User saved = userRepository.save(user);
        auditService.log("USER_CREATED", null, "User", saved.getId(), "Admin created user: " + saved.getUsername() + " with role " + saved.getRole());
        return new UserDto(saved);
    }

    public UserDto updateUser(Long id, String fullName, String email, Role role, Boolean active, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (fullName != null && !fullName.trim().isEmpty()) user.setFullName(fullName.trim());
        if (email != null && !email.trim().isEmpty()) user.setEmail(email.trim());
        if (role != null) user.setRole(role);
        if (active != null) user.setActive(active);
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(password.trim()));
        }

        User updated = userRepository.save(user);
        auditService.log("USER_UPDATED", null, "User", updated.getId(), "User updated: " + updated.getUsername() + " (role: " + updated.getRole() + ", active: " + updated.isActive() + ")");
        return new UserDto(updated);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        userRepository.delete(user);
        auditService.log("USER_DELETED", null, "User", id, "Admin deleted user: " + user.getUsername());
    }
}
