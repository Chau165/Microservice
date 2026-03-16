package service.CSFC.CSFC_auth_service.service;

import org.springframework.transaction.annotation.Transactional;
import service.CSFC.CSFC_auth_service.common.response.BaseResponse;
import service.CSFC.CSFC_auth_service.model.dto.request.CreateUserRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.UserResponse;

import java.util.UUID;

public interface UserService
{
    UserResponse getCurrentUser(String email);

    void deleteUserByAdmin(UUID userId);
    void deActivateUserByAdmin(UUID userId);
    UserResponse CreateUserWithRoleByAdmin(CreateUserRequest request);
}
