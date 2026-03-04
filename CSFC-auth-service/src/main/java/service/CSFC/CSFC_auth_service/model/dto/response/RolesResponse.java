package service.CSFC.CSFC_auth_service.model.dto.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesResponse {
    private int id;
    private String roleName;
    private LocalDateTime createDate;
}
