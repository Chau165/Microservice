package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.Data;

@Data
public class ApplyCouponRequest {
    private long customerId;
    private String couponCode;
    private Double orderAmount;
}
