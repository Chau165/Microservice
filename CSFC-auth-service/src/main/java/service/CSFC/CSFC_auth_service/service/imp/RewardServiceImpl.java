package service.CSFC.CSFC_auth_service.service.imp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import service.CSFC.CSFC_auth_service.model.dto.request.RewardRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.RewardResponse;
import service.CSFC.CSFC_auth_service.model.entity.Reward;
import service.CSFC.CSFC_auth_service.repository.RewardRepository;
import service.CSFC.CSFC_auth_service.service.FileStorageService;
import service.CSFC.CSFC_auth_service.service.RewardService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {
    @Autowired
    private RewardRepository rewardRepository;
    @Autowired
    private FileStorageService fileStorageService;

    private final String UPLOAD_DIR = "/uploads/rewards/";

    @Override
    public List<RewardResponse> getAllReward() {
        List<Reward> rewards = rewardRepository.findAll();

        return rewards.stream().map(reward -> {
            RewardResponse res = new RewardResponse();
            res.setId(reward.getId());
            res.setFranchiseId(reward.getFranchiseId());
            res.setName(reward.getName());
            res.setRequiredPoints(reward.getRequiredPoints());
            res.setDescription(reward.getDescription());
            res.setActive(reward.getIsActive());
            res.setImageUrl(reward.getImageUrl());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RewardResponse> getActiveRewards() {
        List<Reward> rewards = rewardRepository.findByIsActiveTrue();

        return rewards.stream().map(reward -> {
            RewardResponse res = new RewardResponse();
            res.setId(reward.getId());
            res.setFranchiseId(reward.getFranchiseId());
            res.setName(reward.getName());
            res.setRequiredPoints(reward.getRequiredPoints());
            res.setDescription(reward.getDescription());
            res.setActive(reward.getIsActive());
            res.setImageUrl(reward.getImageUrl());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public Reward createReward(RewardRequest request, MultipartFile imageFile) {
        String fileName = fileStorageService.saveImage(imageFile);

        Reward reward = new Reward();
        reward.setFranchiseId(request.getFranchiseId());
        reward.setName(request.getName());
        reward.setRequiredPoints(request.getRequiredPoints());
        reward.setDescription(request.getDescription());
        reward.setIsActive(request.getActive());
        reward.setImageUrl(fileName);


        return rewardRepository.save(reward);
    }

    @Override
    public Reward updateReward(Long rewardId, RewardRequest request, MultipartFile imageFile) {
        Reward existingReward = rewardRepository.findById(rewardId).
                orElseThrow(() -> new RuntimeException("Reward not found with id: " + rewardId));

        String fileName = fileStorageService.updateImage(existingReward.getImageUrl(), imageFile);
        existingReward.setFranchiseId(request.getFranchiseId());
        existingReward.setName(request.getName());
        existingReward.setRequiredPoints(request.getRequiredPoints());
        existingReward.setDescription(request.getDescription());
        existingReward.setIsActive(request.getActive());
        existingReward.setImageUrl(fileName);

        return rewardRepository.save(existingReward);
    }

    @Override
    public void deleteReward(Long rewardId) {
        Reward existingReward = rewardRepository.findById(rewardId).
                orElseThrow(() -> new RuntimeException("Reward not found with id: " + rewardId));
        fileStorageService.deleteImage(existingReward.getImageUrl());
        rewardRepository.delete(existingReward);
    }

    @Override
    public Reward getRewardById(Long rewardId) {
        return rewardRepository.findById(rewardId).orElseThrow(() ->
                new RuntimeException("Reward not found with id: " + rewardId));
    }
}
