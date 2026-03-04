package service.CSFC.CSFC_auth_service.mapper;

import org.springframework.stereotype.Component;
import service.CSFC.CSFC_auth_service.model.dto.response.RolesResponse;
import service.CSFC.CSFC_auth_service.model.entity.Roles;

import java.time.LocalDateTime;

@Component
public class RolesMapper {
    public Roles toEntity(String name) {

        Roles role = new Roles();
        role.setName(name);
        return role;
    }

    public RolesResponse toResponse(Roles role) {
        RolesResponse response = new RolesResponse();
        response.setId(role.getId());
        response.setRoleName(role.getName());
        response.setCreateDate(role.getCreateDate());
        return response;
    }
}
