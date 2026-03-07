//package com.group5.engagement.config;
//
//import com.group5.engagement.constants.ActionType;
//import com.group5.engagement.constants.CustomerStatus;
//import com.group5.engagement.constants.PromotionStatus;
//import com.group5.engagement.constants.TierName;
//import com.group5.engagement.entity.*;
//import com.group5.engagement.repository.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.LocalDateTime;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class DataInitializer {
//
//    @Bean
//    CommandLineRunner initDatabase(
//            LoyaltyTierRepository tierRepository,
//            CustomerFranchiseRepository customerFranchiseRepository,
//            PointTransactionRepository pointTransactionRepository,
//            PromotionRepository promotionRepository) {
//
//        return args -> {
//
//            if (tierRepository.count() > 0) {
//                log.info("Database already initialized. Skipping data initialization.");
//                return;
//            }
//
//            log.info("Initializing database with sample data...");
//
//            // ===============================
//            // 1. CREATE LOYALTY TIERS
//            // ===============================
//
//            LoyaltyTier bronze = tierRepository.save(LoyaltyTier.builder()
//                    .franchiseId(1L)
//                    .name(TierName.BRONZE)
//                    .minPoints(0)
//                    .tierMultiplier(1.0)
//                    .benefits("Hạng cơ bản - Tích điểm thường")
//                    .build());
//
//            LoyaltyTier silver = tierRepository.save(LoyaltyTier.builder()
//                    .franchiseId(1L)
//                    .name(TierName.SILVER)
//                    .minPoints(500)
//                    .tierMultiplier(1.2)
//                    .benefits("Giảm 5% - Tích điểm x1.2")
//                    .build());
//
//            LoyaltyTier gold = tierRepository.save(LoyaltyTier.builder()
//                    .franchiseId(1L)
//                    .name(TierName.GOLD)
//                    .minPoints(1000)
//                    .tierMultiplier(1.5)
//                    .benefits("Giảm 10% - Tích điểm x1.5 - Ưu tiên hỗ trợ")
//                    .build());
//
//            // ===============================
//            // 2. CREATE CUSTOMER FRANCHISE
//            // ===============================
//
//            CustomerFranchise customer1 = customerFranchiseRepository.save(
//                    CustomerFranchise.builder()
//                            .customerId(1L)
//                            .franchiseId(1L)
//                            .currentPoints(250)
//                            .totalEarnedPoints(300)
//                            .tier(bronze)
//                            .status(CustomerStatus.ACTIVE)
//                            .firstOrderAt(LocalDateTime.of(2026, 1, 15, 10, 0))
//                            .lastOrderAt(LocalDateTime.of(2026, 3, 5, 11, 0))
//                            .build()
//            );
//
//            CustomerFranchise customer2 = customerFranchiseRepository.save(
//                    CustomerFranchise.builder()
//                            .customerId(2L)
//                            .franchiseId(1L)
//                            .currentPoints(1300)
//                            .totalEarnedPoints(1500)
//                            .tier(gold)
//                            .status(CustomerStatus.ACTIVE)
//                            .firstOrderAt(LocalDateTime.of(2026, 1, 10, 9, 0))
//                            .lastOrderAt(LocalDateTime.of(2026, 3, 2, 16, 0))
//                            .build()
//            );
//
//            customerFranchiseRepository.save(
//                    CustomerFranchise.builder()
//                            .customerId(3L)
//                            .franchiseId(1L)
//                            .currentPoints(100)
//                            .totalEarnedPoints(100)
//                            .tier(bronze)
//                            .status(CustomerStatus.ACTIVE)
//                            .firstOrderAt(LocalDateTime.of(2026, 2, 20, 11, 0))
//                            .lastOrderAt(LocalDateTime.of(2026, 2, 28, 15, 0))
//                            .build()
//            );
//
//            // ===============================
//            // 3. CREATE POINT TRANSACTIONS
//            // ===============================
//
//            pointTransactionRepository.save(PointTransaction.builder()
//                    .customerFranchise(customer1)
//                    .amount(100)
//                    .actionType(ActionType.EARN)
//                    .referenceId("ORDER123")
//                    .expiryDate(LocalDateTime.of(2027, 3, 1, 23, 59))
//                    .build());
//
//            pointTransactionRepository.save(PointTransaction.builder()
//                    .customerFranchise(customer1)
//                    .amount(-50)
//                    .actionType(ActionType.REDEEM)
//                    .referenceId("REWARD5")
//                    .build());
//
//            pointTransactionRepository.save(PointTransaction.builder()
//                    .customerFranchise(customer1)
//                    .amount(200)
//                    .actionType(ActionType.EARN)
//                    .referenceId("ORDER456")
//                    .expiryDate(LocalDateTime.of(2027, 3, 5, 23, 59))
//                    .build());
//
//            pointTransactionRepository.save(PointTransaction.builder()
//                    .customerFranchise(customer2)
//                    .amount(500)
//                    .actionType(ActionType.EARN)
//                    .referenceId("ORDER111")
//                    .expiryDate(LocalDateTime.of(2027, 2, 15, 23, 59))
//                    .build());
//
//            pointTransactionRepository.save(PointTransaction.builder()
//                    .customerFranchise(customer2)
//                    .amount(-200)
//                    .actionType(ActionType.REDEEM)
//                    .referenceId("REWARD10")
//                    .build());
//
//            pointTransactionRepository.save(PointTransaction.builder()
//                    .customerFranchise(customer2)
//                    .amount(300)
//                    .actionType(ActionType.EARN)
//                    .referenceId("ORDER222")
//                    .expiryDate(LocalDateTime.of(2027, 3, 1, 23, 59))
//                    .build());
//
//            pointTransactionRepository.save(PointTransaction.builder()
//                    .customerFranchise(customer2)
//                    .amount(700)
//                    .actionType(ActionType.EARN)
//                    .referenceId("ORDER333")
//                    .expiryDate(LocalDateTime.of(2027, 3, 10, 23, 59))
//                    .build());
//
//            // ===============================
//            // 4. CREATE PROMOTIONS
//            // ===============================
//
//            promotionRepository.save(Promotion.builder()
//                    .franchiseId(1L)
//                    .name("Khuyến mãi mùa xuân 2026")
//                    .description("Giảm giá 20% cho tất cả sản phẩm")
//                    .status(PromotionStatus.ACTIVE)
//                    .startDate(LocalDateTime.of(2026, 3, 1, 0, 0))
//                    .endDate(LocalDateTime.of(2026, 3, 31, 23, 59))
//                    .build());
//
//            promotionRepository.save(Promotion.builder()
//                    .franchiseId(1L)
//                    .name("Khuyến mãi hè 2026")
//                    .description("Giảm giá 15% - Chỉ hạng Gold")
//                    .status(PromotionStatus.DRAFT)
//                    .startDate(LocalDateTime.of(2026, 6, 1, 0, 0))
//                    .endDate(LocalDateTime.of(2026, 6, 30, 23, 59))
//                    .build());
//
//            log.info("Database initialization completed successfully!");
//        };
//    }
//}