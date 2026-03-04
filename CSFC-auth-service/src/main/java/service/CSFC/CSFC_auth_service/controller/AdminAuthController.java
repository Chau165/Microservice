package service.CSFC.CSFC_auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.common.response.BaseResponse;
import service.CSFC.CSFC_auth_service.model.dto.request.AssignRoleRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ResetPasswordResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.UserDetailResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.UserListResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.UserResponse;
import service.CSFC.CSFC_auth_service.service.AdminUserService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/auth-users")
public class AdminAuthController {

    private final AdminUserService adminUserService;

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('USER_RESET_PASSWORD')")
    public ResponseEntity<BaseResponse<ResetPasswordResponse>> resetPassword(
            @PathVariable UUID id
    ) {

        ResetPasswordResponse response = adminUserService.resetPassword(id);

        return ResponseEntity.ok(
                BaseResponse.success("Đặt lại mật khẩu thành công", response)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<BaseResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

     Page<UserResponse>  response = adminUserService.getAllUsers(page, size, sortBy, sortDir);

        return ResponseEntity.ok(
                BaseResponse.success("Lấy danh sách người dùng thành công", response)
        );
    }

    // ================= GET USER DETAIL =================

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<BaseResponse<UserDetailResponse>> getUserDetail(
            @PathVariable UUID id
    ) {

        UserDetailResponse response = adminUserService.getUserDetail(id);

        return ResponseEntity.ok(
                BaseResponse.success("Lấy chi tiết người dùng thành công", response)
        );
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('USER_UPDATE_STATUS')")
    public ResponseEntity<BaseResponse<String>> activateUser(
            @PathVariable UUID id
    ) {

        adminUserService.activateUser(id);

        return ResponseEntity.ok(
                BaseResponse.success("Kích hoạt tài khoản thành công", null)
        );
    }


    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('USER_ASSIGN_ROLE')")
    public ResponseEntity<BaseResponse<String>> assignRole(
            @PathVariable UUID id,
            @RequestBody AssignRoleRequest request
    ) {

        adminUserService.assignRole(id, request.getRoleId());

        return ResponseEntity.ok(
                BaseResponse.success("Chỉ định vai trò cho người dùng thành công", null)
        );
    }
}