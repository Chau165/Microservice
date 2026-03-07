package org.example.warehouseservice.mapper;

import org.example.warehouseservice.dto.responseDTO.RequestHistoryResponseDto;
import org.example.warehouseservice.entity.RequestHistory;

public class RequestHistoryMapper {

    public static RequestHistoryResponseDto toHistoryResponseDto(RequestHistory history) {
        if (history == null) {
            return null;
        }

        var request = history.getRequest();

        return new RequestHistoryResponseDto(
                history.getHistoryId(),
                request != null && request.getId() != null ? String.valueOf(request.getId()) : null,
                history.getItem() != null ? history.getItem().getName() : null,
                request != null ? request.getRequestType() : null,
                request != null && request.getQuantity() != null ? request.getQuantity().intValue() : null,
                request != null && request.getHandledBy() != null ? String.valueOf(request.getHandledBy()) : null,
                history.getCompletedDate()
        );
    }
}