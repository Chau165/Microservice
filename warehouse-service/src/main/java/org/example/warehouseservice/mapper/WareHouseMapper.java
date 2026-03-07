package org.example.warehouseservice.mapper;

import org.example.warehouseservice.dto.requestDTO.WarehouseRequestDto;
import org.example.warehouseservice.dto.responseDTO.WarehouseResponseDto;
import org.example.warehouseservice.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WareHouseMapper {

    // 1. Entity to DTO: Extract 'name' from Location object
    @Mapping(source = "location.name", target = "location")
    WarehouseRequestDto toWarehouseRequestDto(Warehouse warehouse);

    @Mapping(source = "location.name", target = "location")
    WarehouseResponseDto toWarehouseResponseDto(Warehouse warehouse);

    // 2. DTO to Entity: Ignore Location to prevent mapping errors
    @Mapping(target = "location", ignore = true)
    Warehouse toWarehouse(WarehouseRequestDto warehouseRequestDto);

    @Mapping(target = "location", ignore = true)
    Warehouse toWarehouse(WarehouseResponseDto warehouseResponseDto);
}