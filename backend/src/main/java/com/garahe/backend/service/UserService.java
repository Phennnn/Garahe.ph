package com.garahe.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garahe.backend.entity.User;
import com.garahe.backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Register a new user
     */
    @Transactional
    public User registerUser(String fullName, String email, String password, String phone) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));  // Hash the password
        user.setPhone(phone);
        user.setRole(User.UserRole.USER);
        user.setActive(true);

        return userRepository.save(user);
    }

    /**
     * Login user
     */
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if password matches
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Check if account is active
        if (!user.isActive()) {
            throw new RuntimeException("Account is disabled");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return user;
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by ID
     */
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Update user profile
     */
    @Transactional
    public User updateProfile(Long userId, String fullName, String phone) {
        User user = findById(userId);
        user.setFullName(fullName);
        user.setPhone(phone);
        return userRepository.save(user);
    }

    /**
     * Change password
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = findById(userId);

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Get all users (admin only)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Deactivate user account
     */
    @Transactional
    public void deactivateUser(Long userId) {
        User user = findById(userId);
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Activate user account
     */
    @Transactional
    public void activateUser(Long userId) {
        User user = findById(userId);
        user.setActive(true);
        userRepository.save(user);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}