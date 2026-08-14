package vn.gaspro.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.gaspro.api.dto.request.LoginRequest;
import vn.gaspro.api.dto.request.RegisterRequest;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.dto.response.AuthResponse;
import vn.gaspro.api.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String tokenHeader) {
        // Cắt bỏ "Bearer " (7 ký tự)
        String token = tokenHeader.substring(7);
        authService.logout(token);
        return ApiResponse.success(null);
    }
}
