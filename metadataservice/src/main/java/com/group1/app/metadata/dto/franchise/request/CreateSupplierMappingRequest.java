package com.group1.app.metadata.dto.franchise.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateSupplierMappingRequest {

    private UUID supplierId;

    private String reason;
}
