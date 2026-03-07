package com.group5.engagement.dto.response;

import com.group5.engagement.constants.ActionType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionHistoryResponse {
    private Long id;
    private Integer amount;
    private ActionType actionType;
    private String referenceId;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
}
