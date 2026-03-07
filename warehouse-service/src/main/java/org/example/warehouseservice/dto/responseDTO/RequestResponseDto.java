package org.example.warehouseservice.dto.responseDTO;

import java.time.LocalDateTime;

public record RequestResponseDto(
        Long id,
        String requestType,
        String status,
        String rejectReason,
        Long handledBy,
        LocalDateTime createdDate,
        LocalDateTime updatedDate,
        Long franchiseId,
        Long supplierId,
        Long quantity,
        String itemName
) {
}