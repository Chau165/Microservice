package org.example.warehouseservice.service;

import org.example.warehouseservice.dto.responseDTO.WarehouseResponseDto;

import java.util.List;

public interface WarehouseService {
    List<WarehouseResponseDto> getAllWarehouses();
}
