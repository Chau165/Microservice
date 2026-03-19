package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.dto.franchisestaff.FranchiseDTO;
import com.group1.app.metadata.dto.franchisestaff.StaffWithFranchisesResponse;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.entity.franchise.FranchiseStatus;
import com.group1.app.metadata.entity.franchisestaff.FranchiseStaff;
import com.group1.app.metadata.entity.franchisestaff.FranchiseStaffStatus;
import com.group1.app.metadata.event.franchise.franchisestaff.StaffAssignedToFranchiseEvent;
import com.group1.app.metadata.event.franchise.franchisestaff.StaffRemovedFromFranchiseEvent;
import com.group1.app.metadata.mapper.staff.StaffMapper;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import com.group1.app.metadata.repository.franchisestaff.FranchiseStaffRepository;
import com.group1.app.metadata.service.FranchiseStaffService;
import com.group1.app.shift.entity.Staff;
import com.group1.app.shift.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FranchiseStaffServiceImpl implements FranchiseStaffService {

    private final FranchiseStaffRepository franchiseStaffRepository;
    private final FranchiseRepository franchiseRepository;
    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public FranchiseStaff assignStaff(UUID franchiseId, String staffId) {

        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (franchise.getStatus() == FranchiseStatus.SUSPENDED) {
            throw new ApiException(ErrorCode.FS_003_FRANCHISE_SUSPENDED);
        }

        staffRepository.getStaffById(staffId)
                .orElseThrow(() -> new ApiException(ErrorCode.FS_001_STAFF_NOT_FOUND));

        franchiseStaffRepository
                .findByStaffIdAndStatus(staffId, FranchiseStaffStatus.ACTIVE)
                .ifPresent(fs -> {
                    throw new ApiException(ErrorCode.FS_002_STAFF_ALREADY_ASSIGNED);
                });

        Instant now = Instant.now();

        FranchiseStaff entity = FranchiseStaff.builder()
                .staffId(staffId)
                .franchise(franchise)
                .status(FranchiseStaffStatus.ACTIVE)
                .assignedAt(now)
                .build();

        FranchiseStaff saved = franchiseStaffRepository.save(entity);

        eventPublisher.publishEvent(
                new StaffAssignedToFranchiseEvent(
                        franchiseId,
                        staffId,
                        now
                )
        );

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public StaffWithFranchisesResponse getFranchiseByStaffId(String staffId) {

        List<FranchiseStaff> mappings =
                franchiseStaffRepository.findAllByStaffId(staffId);

        if (mappings.isEmpty()) {
            throw new ApiException(ErrorCode.FS_004_MAPPING_NOT_FOUND);
        }

        List<FranchiseDTO> franchises = mappings.stream()
                .map(FranchiseStaff::getFranchise)
                .map(FranchiseDTO::from)
                .toList();

        Staff staff = staffRepository.getStaffById(staffId)
                .orElseThrow(() -> new ApiException(ErrorCode.FS_001_STAFF_NOT_FOUND));

        return new StaffWithFranchisesResponse(staffMapper.toResponse(staff), franchises);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FranchiseStaff> getAllByFranchiseId(UUID franchiseId) {

        if (!franchiseRepository.existsById(franchiseId)) {
            throw new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND);
        }

        return franchiseStaffRepository.findAllByFranchiseId(franchiseId);
    }

    @Override
    public void removeStaff(UUID franchiseId, String staffId) {

        FranchiseStaff mapping = franchiseStaffRepository
                .findByStaffIdAndFranchiseIdAndStatus(
                        staffId,
                        franchiseId,
                        FranchiseStaffStatus.ACTIVE
                )
                .orElseThrow(() -> new ApiException(ErrorCode.FS_004_MAPPING_NOT_FOUND));

        Instant now = Instant.now();

        mapping.setStatus(FranchiseStaffStatus.INACTIVE);
        mapping.setUnassignedAt(now);

        franchiseStaffRepository.save(mapping);

        eventPublisher.publishEvent(
                new StaffRemovedFromFranchiseEvent(
                        franchiseId,
                        staffId,
                        now
                )
        );
    }
}