package service.CSFC.CSFC_auth_service.service.imp;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.common.exception.InsufficientPointsException;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.model.constants.ActionType;
import service.CSFC.CSFC_auth_service.model.dto.response.RedemptionQRResponse;
import service.CSFC.CSFC_auth_service.model.entity.CustomerFranchise;
import service.CSFC.CSFC_auth_service.model.entity.PointTransaction;
import service.CSFC.CSFC_auth_service.model.entity.Redemption;
import service.CSFC.CSFC_auth_service.model.entity.Reward;
import service.CSFC.CSFC_auth_service.repository.CustomerFranchiseRepository;
import service.CSFC.CSFC_auth_service.repository.PointTransactionRepository;
import service.CSFC.CSFC_auth_service.repository.RedemptionRepository;
import service.CSFC.CSFC_auth_service.repository.RewardRepository;
import service.CSFC.CSFC_auth_service.service.QrCodeService;
import service.CSFC.CSFC_auth_service.service.RedemptionService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static service.CSFC.CSFC_auth_service.model.constants.RedemptionStatus.PENDING;


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

    @Override
    public Optional<Redemption> findByRedemptionCode(String code) {
        return redemptionRepository.findByRedemptionCode(code);
    }

    @Override
    public void save(Redemption redemption) {
        redemptionRepository.save(redemption);
    }
}
