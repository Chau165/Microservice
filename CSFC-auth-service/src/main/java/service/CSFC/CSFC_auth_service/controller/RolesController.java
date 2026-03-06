package service.CSFC.CSFC_auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.common.response.BaseResponse;
import service.CSFC.CSFC_auth_service.model.dto.request.RolesRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.UpdateRolesRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.RolesResponse;
import service.CSFC.CSFC_auth_service.service.RolesService;

@RestController
@RequestMapping("/api/auth-service/roles")
@RequiredArgsConstructor
public class RolesController {

       private final RolesService rolesService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_CREATE')")
    public ResponseEntity<BaseResponse<RolesResponse>> createRole(@RequestBody RolesRequest request) {
           RolesResponse response = rolesService.createRole(request);
           return ResponseEntity.ok(BaseResponse.success("Tạo role thành công", response));
    }
    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_UPDATE')")
    public ResponseEntity<BaseResponse<RolesResponse>> updateRole(@RequestBody UpdateRolesRequest request) {
        RolesResponse response = rolesService.updateRoleById(request);
        return ResponseEntity.ok(BaseResponse.success("Cập nhật role thành công", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VIEW')")
    public ResponseEntity<BaseResponse> getAllRoles() {
        return ResponseEntity.ok(BaseResponse.success("Lấy danh sách role thành công", rolesService.getAllRoles()));
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_DELETE')")
    public ResponseEntity<BaseResponse> deleteRole(@PathVariable int id) {
        rolesService.deleteRoleById(id);
        return ResponseEntity.ok(BaseResponse.success("Xóa role thành công", id));
        }

}
