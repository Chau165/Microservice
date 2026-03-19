package com.group1.app.metadata.dto.franchise.request;

import lombok.Data;

import java.util.UUID;

@Data
public class SupplierMappingDecisionRequest {

    private UUID requestId;

    private String decision;

    private String comment;
}
