package com.payflow.Auth_servce.controllers;

import com.payflow.Auth_servce.dtos.UserDTO;
import com.payflow.Auth_servce.security.UserPrincipal;
import com.payflow.Auth_servce.services.AuthService;
import com.payflow.Auth_servce.util.LoginRequest;
import com.payflow.Auth_servce.util.LoginResponse;
import com.payflow.Auth_servce.util.RegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/greet")
    public String greet() {
        System.out.println("greet");
        return "Hello World";
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(authService.register(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }


    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(authService.getMe(userId));
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Access denied.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal op)) {
            throw new AccessDeniedException("Access denied.");
        }

        return op.getUserId();
    }
}
