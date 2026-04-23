package com.harlen.saas_pedidos.controller;

import com.harlen.saas_pedidos.dto.auth.AuthForgotPasswordRequest;
import com.harlen.saas_pedidos.dto.auth.AuthLoginRequest;
import com.harlen.saas_pedidos.dto.auth.AuthMessageResponse;
import com.harlen.saas_pedidos.dto.auth.AuthRefreshRequest;
import com.harlen.saas_pedidos.dto.auth.AuthRegisterRequest;
import com.harlen.saas_pedidos.dto.auth.AuthResetPasswordRequest;
import com.harlen.saas_pedidos.dto.auth.AuthResponse;
import com.harlen.saas_pedidos.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthRegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody AuthRefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public AuthMessageResponse logout(@Valid @RequestBody AuthRefreshRequest request) {
        return authService.logout(request);
    }

    @PostMapping("/forgot-password")
    public AuthMessageResponse forgotPassword(@Valid @RequestBody AuthForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public AuthMessageResponse resetPassword(@Valid @RequestBody AuthResetPasswordRequest request) {
        return authService.resetPassword(request);
    }

    @GetMapping("/me")
    public AuthResponse me() {
        return authService.me();
    }
}
