package org.example.warehouseservice.service;

import org.example.warehouseservice.dto.responseDTO.RequestHistoryResponseDto;
import org.springframework.data.domain.Page;

public interface RequestHistoryService {
    // S15: View Import / Export History
    Page<RequestHistoryResponseDto> getHistoryWithFilters(String actionType, String ingredientName, String fromDate, String toDate, int page, int size);
}