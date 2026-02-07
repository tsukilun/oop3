package com.socialmedia.controller;

import com.socialmedia.model.User;
import com.socialmedia.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** get all users */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /** get user by id */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    /** create user */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, String> body) {
        int id = userService.createUser(body.get("name"), body.get("email"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", id, "message", "User created"));
    }

    /** update user */
    @PutMapping("/{id}")
    public Map<String, String> updateUser(@PathVariable int id, @RequestBody Map<String, String> body) {
        userService.updateUser(id, body.get("name"), body.get("email"));
        return Map.of("message", "User updated");
    }

    /** delete user */
    @DeleteMapping("/{id}")
    public Map<String, String> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return Map.of("message", "User deleted");
    }
}
