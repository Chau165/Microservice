package service.CSFC.CSFC_auth_service.service;

import service.CSFC.CSFC_auth_service.model.dto.request.*;
import service.CSFC.CSFC_auth_service.model.dto.response.AuthResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.RegisterResponse;

public interface AuthenticationService {

    AuthResponse login(LoginRequest request);

    RegisterResponse register(RegisterRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}