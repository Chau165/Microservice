package org.example.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.warehouseservice.dto.ApiResponse;
import org.example.warehouseservice.dto.requestDTO.RequestRequestDto;
import org.example.warehouseservice.dto.responseDTO.RequestResponseDto;
import org.example.warehouseservice.service.RequestService;
import org.example.warehouseservice.service.RequestImportService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/warehouse-service/requests")
public class RequestController {

    private final RequestService requestService;
    private final RequestImportService requestImportService;

    // S13: View Request Log
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RequestResponseDto>>> getRequests(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<RequestResponseDto> requestPage = requestService.getRequestsWithFilters(type, status, fromDate, toDate, page - 1, size);

        ApiResponse<Page<RequestResponseDto>> response = new ApiResponse<>();
        response.setData(requestPage);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }

    // S14: Accept / Reject Request
    @PostMapping("/{id}/status/{status}")
    public ResponseEntity<ApiResponse<RequestResponseDto>> updateRequestStatus(
            @RequestParam("id") String id,
            @RequestParam("status") String status,
            @RequestParam(required = false) String reason) {

        RequestResponseDto updatedRequest = requestService.updateStatus(id, status, reason);

        ApiResponse<RequestResponseDto> response = new ApiResponse<>();
        response.setData(updatedRequest);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RequestResponseDto>> createRequestImport(@RequestBody RequestRequestDto requestRequestDto) {
        ApiResponse<RequestResponseDto> response = new ApiResponse<>();
        RequestResponseDto requestResponseDto = requestImportService.createRequest(requestRequestDto);
        response.setData(requestResponseDto);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRequestImport(@PathVariable Long id) {
        ApiResponse<Void> response = new ApiResponse<>();
        requestImportService.deleteRequestImport(id);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }
}