package com.ddeleon2022166.BiblioApp.controller;

import com.ddeleon2022166.BiblioApp.dto.request.AuthRequest;
import com.ddeleon2022166.BiblioApp.dto.request.RegisterRequest;
import com.ddeleon2022166.BiblioApp.dto.response.AuthResponse;
import com.ddeleon2022166.BiblioApp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}