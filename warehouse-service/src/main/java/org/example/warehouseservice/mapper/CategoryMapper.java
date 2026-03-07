package org.example.warehouseservice.mapper;

import org.example.warehouseservice.dto.requestDTO.CategoryRequestDto;
import org.example.warehouseservice.dto.responseDTO.CategoryResponseDto;
import org.example.warehouseservice.entity.Category;
import org.example.warehouseservice.entity.Warehouse;
import org.mapstruct.Mapper;


public class CategoryMapper {
    public static Category toCategory(CategoryRequestDto req) {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseId(req.warehouseId());
        return Category.builder()
                .name(req.name())
                .description(req.description())
                .displayOrder(req.displayOrder())
                .warehouse(warehouse)
                .build();
    }

    public static CategoryResponseDto toCategoryResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .status(category.getStatus())
                .warehouseId(category.getWarehouse() != null
                        ? category.getWarehouse().getWarehouseId()
                        : null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
