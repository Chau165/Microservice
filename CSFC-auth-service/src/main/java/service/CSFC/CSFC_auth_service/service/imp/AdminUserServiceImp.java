package service.CSFC.CSFC_auth_service.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.mapper.UserMapper;
import service.CSFC.CSFC_auth_service.model.dto.response.ResetPasswordResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.UserDetailResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.UserListResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.UserResponse;
import service.CSFC.CSFC_auth_service.model.entity.Roles;
import service.CSFC.CSFC_auth_service.model.entity.Users;
import service.CSFC.CSFC_auth_service.model.util.PasswordUtil;
import service.CSFC.CSFC_auth_service.repository.RolesRepository;
import service.CSFC.CSFC_auth_service.repository.UsersRepository;
import service.CSFC.CSFC_auth_service.service.AdminUserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImp implements AdminUserService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    @Override
    public ResetPasswordResponse resetPassword(UUID userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        String rawPassword = PasswordUtil.generateRandomPassword(10);

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setIsFirstLogin(true);

        usersRepository.save(user);

        return new ResetPasswordResponse(user.getId(), rawPassword);
    }


    @Override
    public Page<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);


        return usersRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public UserDetailResponse getUserDetail(UUID userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        return userMapper.toDetailResponse(user);
    }

    @Override
    public void activateUser(UUID userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        user.setIsActive(true);
        usersRepository.save(user);
    }


    @Override
    public void assignRole(UUID userId, Long roleId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Roles role = rolesRepository.findById(roleId.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));

        user.setRole(role);
        usersRepository.save(user);
    }
}