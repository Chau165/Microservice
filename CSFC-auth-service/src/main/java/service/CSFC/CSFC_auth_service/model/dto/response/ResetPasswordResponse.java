package service.CSFC.CSFC_auth_service.model.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ResetPasswordResponse {

    private UUID userId;
    private String tempPassword;
}