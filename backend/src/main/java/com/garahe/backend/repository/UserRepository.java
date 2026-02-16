package com.garahe.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garahe.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email (for login)
    Optional<User> findByEmail(String email);
    
    // Check if email exists (for registration validation)
    boolean existsByEmail(String email);
    
    // Find all active users
    List<User> findByActiveTrue();
    
    // Find users by role
    List<User> findByRole(User.UserRole role);
}