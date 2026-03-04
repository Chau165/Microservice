package service.CSFC.CSFC_auth_service.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.common.response.BaseResponse;
import service.CSFC.CSFC_auth_service.common.security.CustomerUserDetails;
import service.CSFC.CSFC_auth_service.model.dto.request.*;
import service.CSFC.CSFC_auth_service.model.dto.response.AuthResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.RegisterResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.UserResponse;
import service.CSFC.CSFC_auth_service.service.AuthenticationService;
import service.CSFC.CSFC_auth_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")

public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authenticationService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.success("Đăng ký thành công", response));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authenticationService.login(request);
        return ResponseEntity.ok(BaseResponse.success("Đăng nhập thành công", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authenticationService.refreshToken(request);
        return ResponseEntity.ok(BaseResponse.success("Làm mới accessToken thành công", authResponse));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ResponseEntity.ok(BaseResponse.success("Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok(BaseResponse.success("Đặt lại mật khẩu thành công", null));
    }
}
