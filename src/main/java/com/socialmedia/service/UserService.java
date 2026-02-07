package com.socialmedia.service;

import com.socialmedia.exception.BadRequestException;
import com.socialmedia.exception.ResourceNotFoundException;
import com.socialmedia.model.User;
import com.socialmedia.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    public int createUser(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Name is required");
        }
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        return userRepository.save(name.trim(), email.trim());
    }

    public void updateUser(int id, String name, String email) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Name is required");
        }
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        boolean updated = userRepository.update(id, name.trim(), email.trim());
        if (!updated) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
    }

    public void deleteUser(int id) {
        boolean deleted = userRepository.delete(id);
        if (!deleted) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
    }

    public int getUserCount() {
        return userRepository.count();
    }
}
