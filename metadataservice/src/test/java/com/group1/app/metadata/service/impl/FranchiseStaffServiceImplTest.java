package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.entity.franchise.FranchiseStatus;
import com.group1.app.metadata.entity.franchisestaff.FranchiseStaff;
import com.group1.app.metadata.entity.franchisestaff.FranchiseStaffStatus;
import com.group1.app.metadata.mapper.staff.StaffMapper;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import com.group1.app.metadata.repository.franchisestaff.FranchiseStaffRepository;
import com.group1.app.shift.entity.Staff;
import com.group1.app.shift.repository.StaffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseStaffServiceImplTest {

    @InjectMocks
    FranchiseStaffServiceImpl service;

    @Mock
    FranchiseStaffRepository franchiseStaffRepository;

    @Mock
    FranchiseRepository franchiseRepository;

    @Mock
    StaffRepository staffRepository;

    @Mock
    StaffMapper staffMapper;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Test
    void assignStaff_success() {
        UUID franchiseId = UUID.randomUUID();
        String staffId = "staff-1";

        Franchise franchise = new Franchise();
        franchise.setId(franchiseId);
        franchise.setStatus(FranchiseStatus.LIVE);

        when(franchiseRepository.findById(franchiseId)).thenReturn(Optional.of(franchise));
        when(staffRepository.getStaffById(staffId)).thenReturn(Optional.of(new Staff()));
        when(franchiseStaffRepository.findByStaffIdAndStatus(staffId, FranchiseStaffStatus.ACTIVE)).thenReturn(Optional.empty());

        FranchiseStaff saved = FranchiseStaff.builder()
                .staffId(staffId)
                .franchise(franchise)
                .status(FranchiseStaffStatus.ACTIVE)
                .assignedAt(Instant.now())
                .build();

        when(franchiseStaffRepository.save(any())).thenReturn(saved);

        FranchiseStaff result = service.assignStaff(franchiseId, staffId);

        assertNotNull(result);
        assertEquals(staffId, result.getStaffId());
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.franchise.franchisestaff.StaffAssignedToFranchiseEvent.class));
    }

    @Test
    void assignStaff_franchiseNotFound() {
        UUID franchiseId = UUID.randomUUID();
        when(franchiseRepository.findById(franchiseId)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.assignStaff(franchiseId, "s"));
        assertEquals(ErrorCode.FR_404_FRANCHISE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void assignStaff_franchiseSuspended() {
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = new Franchise();
        franchise.setId(franchiseId);
        franchise.setStatus(FranchiseStatus.SUSPENDED);
        when(franchiseRepository.findById(franchiseId)).thenReturn(Optional.of(franchise));

        ApiException ex = assertThrows(ApiException.class, () -> service.assignStaff(franchiseId, "s"));
        assertEquals(ErrorCode.FS_003_FRANCHISE_SUSPENDED, ex.getErrorCode());
    }

    @Test
    void assignStaff_staffNotFound() {
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = new Franchise();
        franchise.setId(franchiseId);
        franchise.setStatus(FranchiseStatus.LIVE);
        when(franchiseRepository.findById(franchiseId)).thenReturn(Optional.of(franchise));
        when(staffRepository.getStaffById("s")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.assignStaff(franchiseId, "s"));
        assertEquals(ErrorCode.FS_001_STAFF_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void assignStaff_alreadyAssigned() {
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = new Franchise();
        franchise.setId(franchiseId);
        franchise.setStatus(FranchiseStatus.LIVE);
        when(franchiseRepository.findById(franchiseId)).thenReturn(Optional.of(franchise));
        when(staffRepository.getStaffById("s")).thenReturn(Optional.of(new Staff()));
        when(franchiseStaffRepository.findByStaffIdAndStatus("s", FranchiseStaffStatus.ACTIVE)).thenReturn(Optional.of(new FranchiseStaff()));

        ApiException ex = assertThrows(ApiException.class, () -> service.assignStaff(franchiseId, "s"));
        assertEquals(ErrorCode.FS_002_STAFF_ALREADY_ASSIGNED, ex.getErrorCode());
    }

    @Test
    void getFranchiseByStaffId_success() {
        String staffId = "s1";
        Franchise f = new Franchise();
        f.setStatus(FranchiseStatus.LIVE);
        f.setId(UUID.randomUUID());
        FranchiseStaff mapping = FranchiseStaff.builder().franchise(f).staffId(staffId).build();
        when(franchiseStaffRepository.findAllByStaffId(staffId)).thenReturn(List.of(mapping));
        when(staffRepository.getStaffById(staffId)).thenReturn(Optional.of(new Staff()));
        when(staffMapper.toResponse(any())).thenReturn(new com.group1.app.shift.dto.response.StaffResponse());

        var resp = service.getFranchiseByStaffId(staffId);
        assertNotNull(resp);
        assertFalse(resp.franchises().isEmpty());
    }

    @Test
    void getFranchiseByStaffId_noMappings() {
        String staffId = "s1";
        when(franchiseStaffRepository.findAllByStaffId(staffId)).thenReturn(List.of());

        ApiException ex = assertThrows(ApiException.class, () -> service.getFranchiseByStaffId(staffId));
        assertEquals(ErrorCode.FS_004_MAPPING_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAllByFranchiseId_notFound() {
        UUID franchiseId = UUID.randomUUID();
        when(franchiseRepository.existsById(franchiseId)).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class, () -> service.getAllByFranchiseId(franchiseId));
        assertEquals(ErrorCode.FR_404_FRANCHISE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void removeStaff_success() {
        UUID franchiseId = UUID.randomUUID();
        String staffId = "s1";
        FranchiseStaff mapping = FranchiseStaff.builder()
                .staffId(staffId)
                .franchise(new Franchise())
                .status(FranchiseStaffStatus.ACTIVE)
                .build();

        when(franchiseStaffRepository.findByStaffIdAndFranchiseIdAndStatus(staffId, franchiseId, FranchiseStaffStatus.ACTIVE))
                .thenReturn(Optional.of(mapping));

        service.removeStaff(franchiseId, staffId);

        verify(franchiseStaffRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(com.group1.app.metadata.event.franchise.franchisestaff.StaffRemovedFromFranchiseEvent.class));
    }

    @Test
    void removeStaff_mappingNotFound() {
        UUID franchiseId = UUID.randomUUID();
        when(franchiseStaffRepository.findByStaffIdAndFranchiseIdAndStatus("s", franchiseId, FranchiseStaffStatus.ACTIVE))
                .thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.removeStaff(franchiseId, "s"));
        assertEquals(ErrorCode.FS_004_MAPPING_NOT_FOUND, ex.getErrorCode());
    }
}

