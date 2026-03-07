package org.example.warehouseservice.service.impl;

import org.example.warehouseservice.dto.responseDTO.RequestHistoryResponseDto;
import org.example.warehouseservice.entity.RequestHistory;
import org.example.warehouseservice.mapper.RequestHistoryMapper;
import org.example.warehouseservice.repository.RequestHistoryRepository;
import org.example.warehouseservice.service.RequestHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RequestHistoryServiceImpl implements RequestHistoryService {

    @Autowired
    private RequestHistoryRepository historyRepository;

    @Override
    public Page<RequestHistoryResponseDto> getHistoryWithFilters(String actionType, String ingredientName, String fromDate, String toDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<RequestHistory> historyPage = historyRepository.findHistoryByFilters(actionType, ingredientName, fromDate, toDate, pageable);

        return historyPage.map(RequestHistoryMapper::toHistoryResponseDto);
    }
}