package com.group5.engagement.client.dto;

import lombok.Data;

@Data
public class ExternalFranchiseResponse {
    private Long id;
    private String name;
    private Boolean isActive; // Franchise còn hoạt động không
}