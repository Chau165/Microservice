package org.example.warehouseservice.service;

import org.example.warehouseservice.dto.responseDTO.RequestResponseDto;
import org.springframework.data.domain.Page;

public interface RequestService {
    Page<RequestResponseDto> getRequestsWithFilters(String type, String status, String fromDate, String toDate, int page, int size);


    RequestResponseDto updateStatus(String id, String status, String reason);
}