package com.group5.engagement.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Data
public class LoyaltyRuleRequest {
    private String name;
    private String eventType;
    private Double pointMultiplier;
    private Integer fixedPoints;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
