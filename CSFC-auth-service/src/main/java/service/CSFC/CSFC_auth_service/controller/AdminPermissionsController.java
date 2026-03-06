package service.CSFC.CSFC_auth_service.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.common.response.BaseResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.AdminPermissionsViewResponse;
import service.CSFC.CSFC_auth_service.service.AdminPermissionsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth-service/admin-permission/roles")
public class AdminPermissionsController {

    private final AdminPermissionsService adminPermissionsService;

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_ASSIGN')")
    public ResponseEntity<BaseResponse<Object>> addPermissionToRole(
            @PathVariable Integer roleId,
            @RequestParam @NotBlank String permissionName
    ) {
        adminPermissionsService.addPermissionToRole(roleId, permissionName.trim().toUpperCase());
        return ResponseEntity.ok(BaseResponse.success("Gán permission cho role thành công", null));
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<BaseResponse<List<AdminPermissionsViewResponse>>> getAllPermissions() {

        List<AdminPermissionsViewResponse> permissions =
                adminPermissionsService.getAllPermissions();

        return ResponseEntity.ok(
                BaseResponse.success("Lấy danh sách permission thành công", permissions)
        );
    }

    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<BaseResponse<List<AdminPermissionsViewResponse>>> getAllPermissionsByRole(
            @PathVariable Integer roleId
    ){
        List<AdminPermissionsViewResponse> permissions =
                adminPermissionsService.getAllPermissionsByRole(roleId);
        return ResponseEntity.ok(BaseResponse.success("Lấy danh sách permission của role thành công", permissions));
    }

}