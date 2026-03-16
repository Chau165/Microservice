package service.CSFC.CSFC_auth_service.common.client.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExternalOrderResponse {
    private String id; // Order ID thường là String (UUID hoặc mã đơn)
    private Long customerId;
    private BigDecimal totalAmount; // Cần cái này để tính điểm (VD: 10k = 1 điểm)
    private String status; // COMPLETED mới được cộng điểm
}