package org.example.warehouseservice.dto.requestDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.example.warehouseservice.enums.CategoryStatus;

@Builder
public record CategoryUpdateRequestDto (
    @NotBlank(message = "Category name must not be empty")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    String name,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    @NotNull(message = "Display order is required")
    @Min(value = 0, message = "Display order must be greater than or equal to 0")
    Integer displayOrder,
    @NotNull(message = "Status is required")
    CategoryStatus status
){
}
