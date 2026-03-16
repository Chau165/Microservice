package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRequest {

    private Long promotionId;

    private String code;

    private String discountType;

    private Double discountValue;

    private Double minOrderValue;

    private Double maxDiscount;

    private Integer usageLimit;

    private Integer userLimit;

    private Long minTierId;

    private Boolean isPublic;
}