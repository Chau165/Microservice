package org.example.warehouseservice.controller;

import org.example.warehouseservice.dto.ApiResponse;
import org.example.warehouseservice.dto.responseDTO.WarehouseResponseDto;
import org.example.warehouseservice.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse-service/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

     @GetMapping("/get-all")
     public ResponseEntity<ApiResponse<List<WarehouseResponseDto>>> getAllWarehouses() {
         List<WarehouseResponseDto> warehouses = warehouseService.getAllWarehouses();
         ApiResponse<List<WarehouseResponseDto>> response = new ApiResponse<>();
         response.setData(warehouses);
         response.setStatus("success");
         return ResponseEntity.ok(response);
     }
}
