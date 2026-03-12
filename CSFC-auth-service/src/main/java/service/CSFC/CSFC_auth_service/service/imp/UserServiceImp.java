package service.CSFC.CSFC_auth_service.service.imp;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.common.exception.BadRequestException;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.infrastructure.client.InternalShiftClient;
import service.CSFC.CSFC_auth_service.mapper.UserMapper;
import service.CSFC.CSFC_auth_service.model.dto.internal.CreateStaffInternalRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.CreateUserRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.UserResponse;
import service.CSFC.CSFC_auth_service.model.entity.Roles;
import service.CSFC.CSFC_auth_service.model.entity.Users;
import service.CSFC.CSFC_auth_service.repository.RolesRepository;
import service.CSFC.CSFC_auth_service.repository.UsersRepository;
import service.CSFC.CSFC_auth_service.service.UserService;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final UserMapper userMapper;
    private final InternalShiftClient internalShiftClient;

    @Override
    public UserResponse getCurrentUser(String email) {
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + email));

        return userMapper.toResponse(users);
    }

    @Override
    @Transactional
    public void deActivateUserByAdmin(UUID userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BadRequestException("Người dùng đã bị vô hiệu hóa");
        }

        user.setIsActive(false);
    }

    @Override
    @Transactional
    public UserResponse CreateUserWithRoleByAdmin(CreateUserRequest request) {

        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email này đã tồn tại trên hệ thống, vui lòng sử dụng email khác");
        }

        Users user = userMapper.toEntityCreateUserWithRoleByAdmin(
                request,
                passwordEncoder.encode("Demo@123")
        );

        Roles role = (request.getRole() == null)
                ? rolesRepository.findByName("USER")
                    .orElseThrow(() ->
                        new BadRequestException("Không tìm thấy role mặc định: USER"))
                : rolesRepository.findByName(request.getRole().getName())
                    .orElseThrow(() ->
                        new BadRequestException("Không tìm thấy role: " +
                                request.getRole().getName()));

        user.setRole(role);
        user.setIsActive(true);
        user.setIsFirstLogin(true);

        Users savedUser = usersRepository.save(user);

        String roleName = savedUser.getRole().getName();

        if ("STAFF".equalsIgnoreCase(roleName)) {
            CreateStaffInternalRequest staffRequest = CreateStaffInternalRequest.builder()
                    .userId(savedUser.getId().toString())
                    .name(savedUser.getName())
                    .email(savedUser.getEmail())
                    .phone(null)
                    .build();
            try {
                internalShiftClient.createStaff(staffRequest);
            } catch (Exception e) {
                log.error("Failed to create staff profile in Shift Service for userId={}: {}",
                        savedUser.getId(), e.getMessage());
                // Xóa user vừa tạo để đảm bảo tính nhất quán (best-effort rollback)
                usersRepository.delete(savedUser);
                throw new BadRequestException(
                        "Tạo tài khoản thất bại: không thể tạo hồ sơ nhân viên ở Shift Service");
            }
        }

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteUserByAdmin(UUID id) {

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new BadRequestException("Phải vô hiệu hóa người dùng trước khi xóa");
        }

        usersRepository.delete(user);
    }
}
