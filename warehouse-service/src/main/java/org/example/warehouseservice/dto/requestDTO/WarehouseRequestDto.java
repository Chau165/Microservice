package org.example.warehouseservice.dto.requestDTO;

import java.time.LocalDate;

public record WarehouseRequestDto(
        Long id,
        String name,
        String location,
        String address,
        String status,
        LocalDate createdAt,
        LocalDate updatedAt,
        Long LocationId
) {
}
