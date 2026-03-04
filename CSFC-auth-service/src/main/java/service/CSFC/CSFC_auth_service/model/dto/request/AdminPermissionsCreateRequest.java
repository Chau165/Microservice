package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminPermissionsCreateRequest {
    private String name;
    private String description;
    private LocalDateTime creationDate;
}
