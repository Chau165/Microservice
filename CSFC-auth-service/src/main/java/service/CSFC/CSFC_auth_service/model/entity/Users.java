package service.CSFC.CSFC_auth_service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private UUID id;
    private String name;
    private String email;
    private String password;
    private LocalDateTime createDate;
    private String address;
    private Boolean isFirstLogin;
    private Boolean isActive;
    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    private String refreshToken;
    @ManyToOne
    @JoinColumn(name = "id_role")
    private Roles role;
}
