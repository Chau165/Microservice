package com.group1.app.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageableMapper {

    /**
     * Tạo đối tượng Pageable từ các tham số request.
     *
     * @param page         Số trang hiện tại.
     * @param size         Kích thước trang.
     * @param sort         Chuỗi sort (format: field,direction).
     * @param fieldMapping Map ánh xạ từ DTO field sang Entity field.
     * @param defaultField Trường sắp xếp mặc định.
     * @return Đối tượng Pageable đã được chuẩn hóa.
     */
    public static Pageable createPageable(int page, int size, String sort, Map<String, String> fieldMapping, String defaultField) {
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, defaultField));
        }

        String[] parts = sort.split(",");
        List<Sort.Order> orders = new ArrayList<>();

        // Duyệt qua mảng theo cặp (field, direction)
        for (int i = 0; i < parts.length; i += 2) {
            String fieldKey = parts[i].trim().toLowerCase();
            String entityField = fieldMapping.getOrDefault(fieldKey, defaultField);

            Sort.Direction direction = Sort.Direction.DESC;
            if (i + 1 < parts.length) {
                direction = parts[i + 1].trim().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            }

            orders.add(new Sort.Order(direction, entityField));
        }

        return PageRequest.of(page, size, Sort.by(orders));
    }
}
