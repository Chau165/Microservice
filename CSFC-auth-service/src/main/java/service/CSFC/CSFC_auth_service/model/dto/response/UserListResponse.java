package service.CSFC.CSFC_auth_service.model.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserListResponse {
    private List<UserResponse> users;
}