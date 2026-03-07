package org.example.warehouseservice.mapper;

import org.example.warehouseservice.dto.requestDTO.RequestRequestDto;
import org.example.warehouseservice.dto.responseDTO.RequestResponseDto;
import org.example.warehouseservice.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    RequestResponseDto toRequestResponseDto(Request request);

    // Request DTO to Entity
    Request toEntity(RequestRequestDto requestDto);

    // Entity to Request DTO (If needed for forms/updates)
    RequestRequestDto toRequestDto(Request request);

    // Response DTO to Entity (Rarely used, but kept for consistency)
    Request toEntity(RequestResponseDto responseDto);
}