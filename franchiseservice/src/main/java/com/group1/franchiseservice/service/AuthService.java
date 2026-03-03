package com.group1.franchiseservice.service;

import com.group1.franchiseservice.model.dto.LoginRequest;
import com.group1.franchiseservice.model.dto.LoginResponse;
import com.group1.franchiseservice.model.dto.RegisterRequest;

public interface AuthService {
    public LoginResponse login(LoginRequest request);
    void logout(String refreshToken);
    LoginResponse refresh(String refreshToken);
    void register(RegisterRequest request);
}
