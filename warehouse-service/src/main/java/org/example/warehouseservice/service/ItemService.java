package org.example.warehouseservice.service;

import org.example.warehouseservice.dto.requestDTO.ItemRequestDto;
import org.example.warehouseservice.dto.responseDTO.ItemResponseDto;
import org.example.warehouseservice.entity.Item;

import java.util.List;

public interface ItemService {
    public ItemResponseDto save (ItemRequestDto itemRequestDto);
    public ItemResponseDto update (Long id, ItemRequestDto itemRequestDto);

    public Item isExistedByName (String name,Long warehouseId);


    public List<ItemResponseDto> getAll (Long warehouseId);

    public ItemResponseDto getById (Long id);
    public List<ItemResponseDto> getAllByName (String name,Long warehouseId);
    public List<ItemResponseDto> getAllByPrice (double minPrice, double maxPrice,Long warehouseId);
    public List<ItemResponseDto> getAllBySupplierName (String supplierName,Long warehouseId);
    public List<ItemResponseDto> getAllByCategory (String categoryName,Long warehouseId);
    public List<ItemResponseDto> getAllByLocation (String location);
    public ItemResponseDto deleteItem(Long itemId);
}
