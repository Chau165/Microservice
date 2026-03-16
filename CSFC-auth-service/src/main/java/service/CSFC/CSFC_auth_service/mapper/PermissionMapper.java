package service.CSFC.CSFC_auth_service.mapper;

import org.springframework.stereotype.Component;
import service.CSFC.CSFC_auth_service.model.dto.response.AdminPermissionsViewResponse;
import service.CSFC.CSFC_auth_service.model.entity.Permission;

@Component
public class PermissionMapper {

    public AdminPermissionsViewResponse toResponse(Permission permission) {
        return AdminPermissionsViewResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}