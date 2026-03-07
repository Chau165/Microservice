package org.example.warehouseservice.dto.responseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record LocationResponseDto(
        Long id,
        String name,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<WarehouseResponseDto> warehouseResponseDto
) {
}
