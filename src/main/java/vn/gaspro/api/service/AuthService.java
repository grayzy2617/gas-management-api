package vn.gaspro.api.service;

import vn.gaspro.api.dto.request.LoginRequest;
import vn.gaspro.api.dto.request.RegisterRequest;
import vn.gaspro.api.dto.response.AuthResponse;

import vn.gaspro.api.dto.request.RefreshTokenRequest;
import vn.gaspro.api.dto.response.UserResponse;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String token);
    AuthResponse refreshToken(RefreshTokenRequest request);
    UserResponse getMyProfile(String phone);
}
