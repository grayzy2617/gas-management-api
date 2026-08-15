package vn.gaspro.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.gaspro.api.dto.request.LoginRequest;
import vn.gaspro.api.dto.request.RegisterRequest;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.dto.response.AuthResponse;
import vn.gaspro.api.dto.response.UserResponse;
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
        String token = tokenHeader.substring(7);
        authService.logout(token);
        return ApiResponse.success(null);
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody @Valid vn.gaspro.api.dto.request.RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    public ApiResponse<vn.gaspro.api.dto.response.UserResponse> getMyProfile() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
     UserResponse response = authService.getMyProfile(phone);
        return ApiResponse.success(response);
    }
}
