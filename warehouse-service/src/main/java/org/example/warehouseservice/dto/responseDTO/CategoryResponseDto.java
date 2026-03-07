package org.example.warehouseservice.dto.responseDTO;


import lombok.Builder;
import org.example.warehouseservice.enums.CategoryStatus;
import java.time.LocalDateTime;

@Builder
public record CategoryResponseDto(
        Long id,
        String name,
        String description,
        Integer displayOrder,
        CategoryStatus status,
        Long warehouseId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
