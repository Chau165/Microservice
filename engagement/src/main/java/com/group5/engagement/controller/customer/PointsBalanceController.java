package com.group5.engagement.controller.customer;

import com.group5.engagement.dto.request.PointsBalanceRequest;
import com.group5.engagement.dto.response.PointsBalanceResponse;
import com.group5.engagement.service.PointsBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/engagement/points")
@Tag(name = "Points Balance", description = "API for viewing customer points balance")
public class PointsBalanceController {

    @Autowired
    private PointsBalanceService pointsBalanceService;

    @GetMapping("/balance")
    @Operation(summary = "Get Points Balance (Query Params)",
               description = "Retrieve current points balance and tier information for a customer at a specific franchise using query parameters")
    public ResponseEntity<PointsBalanceResponse> getPointsBalance(
            @Parameter(description = "Customer ID", required = true)
            @RequestParam Long customerId,
            @Parameter(description = "Franchise ID", required = true)
            @RequestParam Long franchiseId) {

        PointsBalanceResponse pointsBalance = pointsBalanceService.getPointsBalance(customerId, franchiseId);

        if (pointsBalance == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pointsBalance);
    }

    @PostMapping("/balance")
    @Operation(summary = "Get Points Balance (Request Body)",
               description = "Retrieve current points balance and tier information for a customer at a specific franchise using request body")
    public ResponseEntity<PointsBalanceResponse> getPointsBalanceByRequest(
            @Valid @RequestBody PointsBalanceRequest request) {

        PointsBalanceResponse pointsBalance = pointsBalanceService.getPointsBalance(
                request.getCustomerId(),
                request.getFranchiseId()
        );

        if (pointsBalance == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pointsBalance);
    }
}
