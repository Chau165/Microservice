package com.group5.engagement.controller.customer;

import com.group5.engagement.dto.response.ApiResponse;
import com.group5.engagement.dto.response.RedemptionQRResponse;
import com.group5.engagement.service.RedemptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/engagement/redemption")
public class RedemptionController {
    @Autowired
    private RedemptionService redemptionService;

    @PostMapping("/confirm/{rewardId}")
    public ResponseEntity<ApiResponse<RedemptionQRResponse>> confirmRedeem(
            @PathVariable Long rewardId
    ) throws Exception {

        RedemptionQRResponse response =
                redemptionService.confirmRedeem(rewardId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Redeem confirmed successfully")
        );
    }
}

