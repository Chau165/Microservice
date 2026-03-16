package service.CSFC.CSFC_auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.dto.request.RedeemRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.CustomerEngagementResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.RedeemResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.TransactionHistoryResponse;
import service.CSFC.CSFC_auth_service.service.LoyaltyService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Customer Loyalty", description = "API loyalty cho khách hàng: điểm, lịch sử giao dịch, đổi điểm")
@RestController
@RequestMapping("/api/engagement/loyalty")
@RequiredArgsConstructor
public class CustomerLoyaltyController {
    private final LoyaltyService loyaltyService;

    @GetMapping("/customers/{customerId}/franchise/{franchiseId}")
    public ResponseEntity<CustomerEngagementResponse> getCustomerEngagement(
            @PathVariable Long customerId,
            @PathVariable Long franchiseId) {
        CustomerEngagementResponse response = loyaltyService.getCustomerEngagement(customerId, franchiseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customers/{customerId}/franchise/{franchiseId}/transactions")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(
            @PathVariable Long customerId,
            @PathVariable Long franchiseId) {
        List<TransactionHistoryResponse> transactions = loyaltyService.getTransactionHistory(customerId, franchiseId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/redeem")
    public ResponseEntity<RedeemResponse> redeem(
            @RequestBody RedeemRequest request
    ) {
        RedeemResponse response = loyaltyService.redeem(request);
        return ResponseEntity.ok(response);
    }
}
