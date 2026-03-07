package org.example.warehouseservice.service;

import org.example.warehouseservice.dto.requestDTO.RequestRequestDto;
import org.example.warehouseservice.dto.responseDTO.RequestResponseDto;

public interface RequestImportService {
    RequestResponseDto createRequest(RequestRequestDto requestImportRequestDto);
    void deleteRequestImport(Long id);
}
