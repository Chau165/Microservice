package service.CSFC.CSFC_auth_service.service;


import org.springframework.web.multipart.MultipartFile;
import service.CSFC.CSFC_auth_service.model.dto.request.RewardRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.RewardResponse;
import service.CSFC.CSFC_auth_service.model.entity.Reward;

import java.util.List;

public interface RewardService {
    List<RewardResponse> getAllReward();
    List<RewardResponse> getActiveRewards();
    Reward createReward(RewardRequest request, MultipartFile file);
    Reward updateReward(Long rewardId, RewardRequest request, MultipartFile file);
    void deleteReward(Long rewardId);
    Reward getRewardById(Long rewardId);
}
