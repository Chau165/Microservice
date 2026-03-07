package org.example.warehouseservice.dto.responseDTO;

import java.time.LocalDate;
import java.util.List;

public record WarehouseResponseDto(
        Long id,
        String name,
        String location,
        String address,
        String status,
        LocalDate createdAt,
        LocalDate updatedAt,
        List<CategoryResponseDto> categoryResponseDto
) {
}
