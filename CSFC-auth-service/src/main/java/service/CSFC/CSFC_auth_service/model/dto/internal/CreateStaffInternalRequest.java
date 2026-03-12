package service.CSFC.CSFC_auth_service.model.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffInternalRequest {
    private String userId;
    private String name;
    private String email;
    private String phone;
}
