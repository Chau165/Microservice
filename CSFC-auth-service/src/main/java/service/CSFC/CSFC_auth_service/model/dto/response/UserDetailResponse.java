package service.CSFC.CSFC_auth_service.model.dto.response;
import lombok.Data;
import java.util.UUID;

@Data
public class UserDetailResponse {
    private UUID id;
    private String email;
    private String role;
    private Boolean isFirstLogin;
}