//package com.group1.app.metadata.data.contract;
//
//import com.group1.app.metadata.entity.contract.Contract;
//import com.group1.app.metadata.entity.contract.ContractStatus;
//import com.group1.app.metadata.entity.franchise.Franchise;
//import com.group1.app.metadata.repository.contract.ContractRepository;
//import com.group1.app.metadata.repository.franchise.FranchiseRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class ContractSeeder implements ApplicationRunner {
//
//    private final ContractRepository contractRepository;
//    private final FranchiseRepository franchiseRepository;
//
//    @Override
//    public void run(ApplicationArguments args) {
//
//        if (contractRepository.count() > 0) return;
//
//        // Lấy franchise có sẵn
//        Franchise franchise = franchiseRepository.findAll()
//                .stream()
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("No franchise found for seeding"));
//
//        contractRepository.saveAll(List.of(
//
//                Contract.builder()
//                        .contractNumber("CN-2025-001")
//                        .franchise(franchise)
//                        .startDate(LocalDate.now())
//                        .endDate(LocalDate.now().plusYears(1))
//                        .royaltyRate(new BigDecimal("5.0"))
//                        .status(ContractStatus.ACTIVE)
//                        .autoOrderEnabled(true)
//                        .createdBy("system-seeder")
//                        .build(),
//
//                Contract.builder()
//                        .contractNumber("CN-2025-002")
//                        .franchise(franchise)
//                        .startDate(LocalDate.now().plusMonths(1))
//                        .endDate(LocalDate.now().plusYears(2))
//                        .royaltyRate(new BigDecimal("7.5"))
//                        .status(ContractStatus.DRAFT)
//                        .autoOrderEnabled(true)
//                        .createdBy("system-seeder")
//                        .build()
//        ));
//    }
//}