package org.example.warehouseservice.controller;

import org.example.warehouseservice.dto.ApiResponse;
import org.example.warehouseservice.dto.responseDTO.RequestHistoryResponseDto;
import org.example.warehouseservice.service.RequestHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/warehouse-service/history")
public class RequestHistoryController {

    @Autowired
    private RequestHistoryService historyService;

    // S15: View Import / Export History
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RequestHistoryResponseDto>>> getHistoryLogs(
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String ingredient,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<RequestHistoryResponseDto> historyPage = historyService.getHistoryWithFilters(actionType, ingredient, fromDate, toDate, page - 1, size);

        ApiResponse<Page<RequestHistoryResponseDto>> response = new ApiResponse<>();
        response.setData(historyPage);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }
}