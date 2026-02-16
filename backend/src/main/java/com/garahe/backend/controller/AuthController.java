package com.garahe.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.garahe.backend.entity.User;
import com.garahe.backend.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        try {
            String fullName = userData.get("fullName");
            String email = userData.get("email");
            String password = userData.get("password");
            String phone = userData.get("phone");

            // Validate input
            if (fullName == null || email == null || password == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "All fields are required"));
            }

            // Register user
            User user = userService.registerUser(fullName, email, password, phone);

            // Return user data (without password)
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("role", user.getRole());
            response.put("message", "Registration successful");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Login user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            // Validate input
            if (email == null || password == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email and password are required"));
            }

            // Login user
            User user = userService.login(email, password);

            // Return user data (without password)
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("role", user.getRole());
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Check if email exists (for validation)
     * GET /api/auth/check-email?email=xxx
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Get user profile
     * GET /api/auth/profile/{userId}
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("role", user.getRole());
            response.put("createdAt", user.getCreatedAt());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update user profile
     * PUT /api/auth/profile/{userId}
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestBody Map<String, String> profileData) {
        try {
            String fullName = profileData.get("fullName");
            String phone = profileData.get("phone");

            User user = userService.updateProfile(userId, fullName, phone);

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("message", "Profile updated successfully");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Change password
     * POST /api/auth/change-password/{userId}
     */
    @PostMapping("/change-password/{userId}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> passwordData) {
        try {
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Both old and new passwords are required"));
            }

            userService.changePassword(userId, oldPassword, newPassword);

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}