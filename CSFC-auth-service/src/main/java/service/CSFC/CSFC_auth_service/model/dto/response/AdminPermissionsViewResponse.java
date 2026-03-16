package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminPermissionsViewResponse {
    private int id;
    private String name;
    private String description;
}