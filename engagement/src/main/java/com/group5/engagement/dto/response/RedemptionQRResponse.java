package com.group5.engagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RedemptionQRResponse {
    private  String redemtionCode;
    private String qrImage;
}
