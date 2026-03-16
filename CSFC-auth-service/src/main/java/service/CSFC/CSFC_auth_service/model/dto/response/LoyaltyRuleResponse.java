package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LoyaltyRuleResponse {
    private Long id;
    private Long franchiseId;
    private String name;
    private String eventType;
    private Double pointMultiplier;
    private Integer fixedPoints;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
