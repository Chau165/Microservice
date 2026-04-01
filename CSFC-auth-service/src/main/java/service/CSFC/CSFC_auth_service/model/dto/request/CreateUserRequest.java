package service.CSFC.CSFC_auth_service.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.CSFC.CSFC_auth_service.model.entity.Roles;

import javax.management.relation.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ, vui lòng nhập lại")
    private String email;
    @NotBlank(message="Họ và tên không được để trống")
    private String name;
    private String address;
    private String phone;

    private Roles role;
}
