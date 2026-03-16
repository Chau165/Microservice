package service.CSFC.CSFC_auth_service.mapper;

import org.springframework.stereotype.Component;
import service.CSFC.CSFC_auth_service.model.dto.request.CreateUserRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.RegisterRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.RegisterResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.UserDetailResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.UserResponse;
import service.CSFC.CSFC_auth_service.model.entity.Users;

@Component
public class UserMapper {
    public Users toEntity(RegisterRequest request, String encodePassword) {
        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setPassword(encodePassword);
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setIsActive(true);
        user.setIsFirstLogin(true);
        return user;
    }
    public Users toEntityCreateUserWithRoleByAdmin(CreateUserRequest request, String encodedPassword){
        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setPassword(encodedPassword);
        return user;
    }
    public UserResponse toResponse(Users user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAddress(user.getAddress());
        return response;
    }

    public UserDetailResponse toDetailResponse(Users user) {
        UserDetailResponse response = new UserDetailResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole() != null ? user.getRole().getName() : null);
        response.setIsFirstLogin(user.getIsFirstLogin());
        return response;
    }
}
