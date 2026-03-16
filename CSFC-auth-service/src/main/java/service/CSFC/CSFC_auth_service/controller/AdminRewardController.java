package service.CSFC.CSFC_auth_service.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.CSFC.CSFC_auth_service.model.dto.request.RewardRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ApiResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.RewardResponse;
import service.CSFC.CSFC_auth_service.model.entity.Reward;
import service.CSFC.CSFC_auth_service.service.RewardService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Admin - Reward Management", description = "Admin quản lý phần thưởng (rewards)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/engagement/admin/rewards")
public class AdminRewardController {

    private final RewardService rewardService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Reward>> createReward(@Valid @RequestPart("reward") RewardRequest request,
                                                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

       Reward reward = rewardService.createReward(request, imageFile);

       return ResponseEntity.ok(
                ApiResponse.success(reward, "Reward created successfully")
       );
   }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Reward>> updateReward(
            @PathVariable Long id,
            @Valid @RequestPart("reward") RewardRequest request,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        Reward updated = rewardService.updateReward(id, request, imageFile);

        return ResponseEntity.ok(
                ApiResponse.success(updated, "Reward updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReward(@PathVariable Long id) {

        rewardService.deleteReward(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Reward deleted successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Reward>> getRewardById(@PathVariable Long id) {
        Reward reward = rewardService.getRewardById(id);
        return ResponseEntity.ok(
                ApiResponse.success(reward, "Reward retrieved successfully")
        );
    }

    @GetMapping("/active")
    public List<RewardResponse> getActiveRewards() {
        return rewardService.getActiveRewards();
    }

    @GetMapping
    public List<RewardResponse> getAllRewards() {
        return rewardService.getAllReward();
    }
}
