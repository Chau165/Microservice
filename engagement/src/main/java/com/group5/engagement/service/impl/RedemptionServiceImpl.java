package com.group5.engagement.service.impl;

import com.group5.engagement.constants.ActionType;
import com.group5.engagement.dto.response.RedemptionQRResponse;
import com.group5.engagement.entity.CustomerFranchise;
import com.group5.engagement.entity.PointTransaction;
import com.group5.engagement.entity.Redemption;
import com.group5.engagement.entity.Reward;
import com.group5.engagement.exception.InsufficientPointsException;
import com.group5.engagement.exception.ResourceNotFoundException;
import com.group5.engagement.repository.CustomerFranchiseRepository;
import com.group5.engagement.repository.PointTransactionRepository;
import com.group5.engagement.repository.RedemptionRepository;
import com.group5.engagement.repository.RewardRepository;
import com.group5.engagement.service.QrCodeService;
import com.group5.engagement.service.RedemptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.group5.engagement.constants.RedemptionStatus.PENDING;

@Service
@RequiredArgsConstructor
public class RedemptionServiceImpl implements RedemptionService {

    private final QrCodeService qrCodeService;
    private final RewardRepository rewardRepository;
    private final CustomerFranchiseRepository customerFranchiseRepository;
    private final RedemptionRepository redemptionRepository;
    private final PointTransactionRepository pointTransactionRepository;


    @Override
    @Transactional
    public RedemptionQRResponse confirmRedeem(Long rewardId) {
        Long userId = getCurrentUserId();
        Redemption redemption = new Redemption();

        //check reward
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reward not found"));

        if (!reward.getIsActive()) {
            throw new ResourceNotFoundException("Reward is inactive");
        }

        //check user point
        CustomerFranchise customerFranchise =
                customerFranchiseRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("User loyalty not found"));

        if (customerFranchise.getCurrentPoints() < reward.getRequiredPoints()) {
            throw new InsufficientPointsException("Not enough points");
        }

        //deduct points
        customerFranchise.setCurrentPoints(
                customerFranchise.getCurrentPoints() - reward.getRequiredPoints()
        );

        customerFranchiseRepository.save(customerFranchise);

        //create redemption code
        String redemptionCode =
                "RDM-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        //save redemption
        redemption.setReward(reward);
        redemption.setPointsUsed(reward.getRequiredPoints());
        redemption.setStatus(PENDING);
        redemption.setRedemptionCode(redemptionCode);
        redemption.setRedeemedAt(LocalDateTime.now());


        redemptionRepository.save(redemption);

        //create point transaction
        PointTransaction pointTransaction = PointTransaction.builder()
                .customerFranchise(customerFranchise)
                .amount(-reward.getRequiredPoints())
                .actionType(ActionType.REDEEM)
                .referenceId("REDEEM_" + redemption.getId())
                .build();

        pointTransactionRepository.save(pointTransaction);

        redemption.setPointTransaction(pointTransaction);
        redemptionRepository.save(redemption);

        //generate QR code
        String qrContent =
                "REDEEM:" + redemptionCode;

        String qrBase64 =
                qrCodeService.generateQrBase64(qrContent);

        //return response
        return new RedemptionQRResponse(
                redemptionCode,
                qrBase64
        );
    }
    // giả lập lấy user login
    private Long getCurrentUserId() {
        return 1L;
    }
}
