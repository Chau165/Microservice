package service.CSFC.CSFC_auth_service.service;



import service.CSFC.CSFC_auth_service.model.dto.response.RedemptionQRResponse;
import service.CSFC.CSFC_auth_service.model.entity.Redemption;

import java.util.Optional;

public interface RedemptionService {
    RedemptionQRResponse confirmRedeem(Long rewardId) ;
    Optional<Redemption> findByRedemptionCode(String redemptionCode);
    void save(Redemption redemption);
}
