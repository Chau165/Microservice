package com.group5.engagement.controller.admin;

import com.group5.engagement.dto.request.CreatePromotionRequest;
import com.group5.engagement.entity.Promotion;
import com.group5.engagement.service.PromotionService;
import com.group5.engagement.constants.PromotionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/engagement/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotion Controller", description = "APIs quản lý khuyến mãi (Promotions)")
public class PromotionController {

    private final PromotionService promotionService;

    @Operation(
        summary = "Tạo promotion mới",
        description = "Tạo một promotion mới với thông tin franchise, tên, mô tả, thời gian bắt đầu và kết thúc"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo promotion thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping
    public ResponseEntity<Promotion> createPromotion(@Valid @RequestBody CreatePromotionRequest request) {
        try {
            System.out.println("=== CREATE PROMOTION REQUEST ===");
            System.out.println("Request: " + request);
            Promotion newPromotion = promotionService.createPromotion(request);
            System.out.println("Created promotion: " + newPromotion);
            return ResponseEntity.status(201).body(newPromotion);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Operation(
        summary = "Lấy tất cả promotions",
        description = "Lấy danh sách tất cả các promotions không phân biệt status hay franchise"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        List<Promotion> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(promotions);
    }

    @Operation(
        summary = "Lấy promotions đang active",
        description = "Lấy danh sách các promotions đang active (trong khoảng thời gian hiện tại). Có thể filter theo franchiseId"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    @GetMapping("/active")
    public ResponseEntity<List<Promotion>> getActivePromotions(
            @Parameter(description = "ID của franchise (optional)", example = "1")
            @RequestParam(required = false) Long franchiseId) {
        List<Promotion> promotions = promotionService.getActivePromotions(franchiseId);
        return ResponseEntity.ok(promotions);
    }

    @Operation(
        summary = "Lấy chi tiết promotion",
        description = "Lấy thông tin chi tiết của một promotion theo ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy promotion")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(
            @Parameter(description = "ID của promotion", example = "1")
            @PathVariable Long id) {
        Promotion promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(promotion);
    }

    @Operation(
        summary = "Cập nhật status của promotion",
        description = "Cập nhật trạng thái của promotion (DRAFT, ACTIVE, INACTIVE, EXPIRED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy promotion")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Promotion> updatePromotionStatus(
            @Parameter(description = "ID của promotion", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Status mới", example = "ACTIVE")
            @RequestParam PromotionStatus status) {
        Promotion promotion = promotionService.updatePromotionStatus(id, status);
        return ResponseEntity.ok(promotion);
    }

}