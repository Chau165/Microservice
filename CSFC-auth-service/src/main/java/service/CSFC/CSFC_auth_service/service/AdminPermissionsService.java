package service.CSFC.CSFC_auth_service.service;

import service.CSFC.CSFC_auth_service.model.dto.response.AdminPermissionsCreateResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.AdminPermissionsViewResponse;

import java.util.List;

public interface AdminPermissionsService {
    void addPermissionToRole(Integer roleId, String permissionName);

    List<AdminPermissionsViewResponse> getAllPermissions();

    List<AdminPermissionsViewResponse> getAllPermissionsByRole(Integer roleId);
}
