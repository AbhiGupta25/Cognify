package com.cognify.controller;

import com.cognify.model.dto.UserAttemptResponse;
import com.cognify.model.dto.UserLoginRequest;
import com.cognify.model.dto.UserSignupRequest;
import com.cognify.model.dto.UserSignupResponse;
import com.cognify.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        UserSignupResponse user = userService.signup(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserSignupResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserSignupResponse user = userService.login(request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}/attempts")
    public ResponseEntity<List<UserAttemptResponse>> getAttempts(@PathVariable Long userId) {
        List<UserAttemptResponse> attempts = userService.getAttemptsForUser(userId);
        return ResponseEntity.ok(attempts);
    }
}
