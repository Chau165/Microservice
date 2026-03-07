package org.example.warehouseservice.dto.responseDTO;

import java.time.LocalDateTime;

public record RequestHistoryResponseDto(
        Long id,
        String requestId,
        String itemName,
        String actionType,
        Integer quantity,
        String performedBy,
        LocalDateTime completedDate
) {
}