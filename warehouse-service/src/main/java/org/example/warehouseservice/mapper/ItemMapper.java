package org.example.warehouseservice.mapper;

import org.example.warehouseservice.dto.requestDTO.ItemRequestDto;
import org.example.warehouseservice.dto.responseDTO.ItemResponseDto;
import org.example.warehouseservice.entity.Item;

import java.util.ArrayList;

public class ItemMapper {
    public static ItemResponseDto toItemResponseDto(Item item) {
        return new ItemResponseDto(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getQuantity(),
                item.getReorderLevel(),
                item.getPrice(),
                item.getSupplierName(),
                item.getStatus(),
                item.getCreatedDate(),
                item.getUpdatedDate(),
                item.getCategory() != null ? item.getCategory().getName() : null,
                new ArrayList<>()
        );
    }
    public static Item toItem(ItemRequestDto itemRequestDto) {
        Item item = new Item();
        item.setName(itemRequestDto.name());
        item.setDescription(itemRequestDto.description());
        item.setQuantity(itemRequestDto.quantity());
        item.setReorderLevel(itemRequestDto.reorderLevel());
        item.setPrice(itemRequestDto.price());
        item.setSupplierName(itemRequestDto.supplierName());
        item.setStatus(itemRequestDto.status());

        return item;
    }
}
