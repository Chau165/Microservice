package service.CSFC.CSFC_auth_service.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.common.exception.BadRequestException;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.common.exception.UnauthorizedException;
import service.CSFC.CSFC_auth_service.common.security.CustomerUserDetails;
import service.CSFC.CSFC_auth_service.mapper.UserMapper;
import service.CSFC.CSFC_auth_service.model.dto.request.*;
import service.CSFC.CSFC_auth_service.model.dto.response.AuthResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.RegisterResponse;

import service.CSFC.CSFC_auth_service.model.entity.Roles;
import service.CSFC.CSFC_auth_service.model.entity.Users;

import service.CSFC.CSFC_auth_service.repository.RolesRepository;
import service.CSFC.CSFC_auth_service.repository.UsersRepository;
import service.CSFC.CSFC_auth_service.service.AuthenticationService;
import service.CSFC.CSFC_auth_service.service.EmailService;
import service.CSFC.CSFC_auth_service.service.JwtService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp implements AuthenticationService {
    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản: " + request.getEmail()));

        CustomerUserDetails userDetails = new CustomerUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        user.setRefreshToken(refreshToken);
        usersRepository.save(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email này đã tồn tại trên hệ thống, vui lòng sử dụng 1 email khác hoặc quên mật khẩu");
        }


        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Users user = userMapper.toEntity(request, encodedPassword);
        Roles defaultRole = new Roles();
        defaultRole.setId(2); // role USER có ID là 2
        user.setRole(defaultRole);
        user.setCreateDate(LocalDateTime.now());
        usersRepository.save(user);

        return RegisterResponse.builder()
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String email = jwtService.extractUsername(refreshToken);

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new UnauthorizedException("RefreshToken không hợp lệ");
        }

        CustomerUserDetails customUserDetails = new CustomerUserDetails(user);
        String newAccessToken = jwtService.generateAccessToken(customUserDetails);

        return buildAuthResponse(user, newAccessToken, refreshToken);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với email: " + request.getEmail()));

        String resetToken = jwtService.generatePasswordResetToken(user.getEmail());
        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;
        emailService.sendEmail(user.getEmail(), resetLink);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String email = jwtService.extractUsername(request.getToken());
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với email: " + email));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setRefreshToken(null);
        usersRepository.save(user);

    }


    private AuthResponse buildAuthResponse(Users user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900000L)
                .user(userMapper.toResponse(user))
                .build();
    }
}
