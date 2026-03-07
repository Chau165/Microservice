package org.example.warehouseservice.service.impl;

import org.example.warehouseservice.dto.responseDTO.WarehouseResponseDto;
import org.example.warehouseservice.entity.Warehouse;
import org.example.warehouseservice.mapper.WareHouseMapper;
import org.example.warehouseservice.repository.WarehouseRepository;
import org.example.warehouseservice.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private WareHouseMapper wareHouseMapper;

    @Override
    public List<WarehouseResponseDto> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(warehouse -> wareHouseMapper.toWarehouseResponseDto(warehouse))
                .toList();
    }
}
