package com.group5.engagement.client.dto;

import lombok.Data;

@Data
public class ExternalUserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String status; // Để check xem user có bị khóa không (ACTIVE/LOCKED)
}