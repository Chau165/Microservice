package service.CSFC.CSFC_auth_service.service;

import org.springframework.data.domain.Page;
import service.CSFC.CSFC_auth_service.model.dto.response.*;

import java.util.UUID;

public interface AdminUserService {

    ResetPasswordResponse resetPassword(UUID userId);

    Page<UserResponse>  getAllUsers(int page, int size, String sortBy, String sortDir);

    UserDetailResponse getUserDetail(UUID userId);

    void activateUser(UUID userId);

    void assignRole(UUID userId, Long roleId);
}