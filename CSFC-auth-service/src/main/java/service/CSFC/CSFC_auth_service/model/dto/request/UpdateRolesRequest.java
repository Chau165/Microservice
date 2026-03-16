package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.Data;

@Data
public class UpdateRolesRequest {
    private int id;
    private String roleName;

}
