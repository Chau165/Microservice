package org.example.warehouseservice.dto.requestDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ItemRequestDto(
        Long id,
        @NotNull(message = "Name cannot be null")
        @NotEmpty(message = "Name cannot be empty")
        String name,
        String description,
        @Min(value = 0, message = "Quantity cannot be negative")
        @NotNull(message = "Quantity cannot be null")
        int quantity,
        @NotNull(message = "Reorder level cannot be null")
        @Min(value = 0, message = "Reorder level cannot be negative")
        int reorderLevel,
        @NotNull(message = "Price cannot be null")
        @Min(value = 0, message = "Price cannot be negative")
        double price,
        @NotNull(message = "Supplier name cannot be null")
        @NotEmpty(message = "Supplier name cannot be empty")
        String supplierName,
        @NotEmpty(message = "Status cannot be empty")
        String status,
        @NotNull(message = "Category ID cannot be null")
        Long categoryId
) {
}
