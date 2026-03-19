package com.group1.app.metadata.service;

import com.group1.app.metadata.dto.franchise.response.WarehouseMappingResponse;
import com.group1.app.metadata.entity.franchise.FranchiseWarehouseMapping;

import java.util.List;
import java.util.UUID;

public interface FranchiseWarehouseMappingService {

    WarehouseMappingResponse updateWarehouseMapping(UUID franchiseId, String warehouseId, String changedBy);

    List<FranchiseWarehouseMapping> getAllByFranchiseId(UUID franchiseId);
}
