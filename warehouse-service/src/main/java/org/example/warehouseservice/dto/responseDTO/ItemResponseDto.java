package org.example.warehouseservice.dto.responseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ItemResponseDto(
        Long id,
        String name,
        String description,
        int quantity,
        int reorderLevel,
        double price,
        String supplierName,
        String status,
        LocalDateTime createdDate,
        LocalDateTime updatedDate,
        String catergoryName,
        List<String> imageUrls
) {
}
