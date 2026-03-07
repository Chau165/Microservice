package org.example.warehouseservice.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private Map<String, String> metadata;
    private Map<String, List<String>> errors;
    private T data;
    private String status;
}
