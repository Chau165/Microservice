package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RedemptionQRResponse {
    private  String redemptionCode;
    private String qrImage;
}
