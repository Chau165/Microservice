package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.dto.franchise.request.UpdateOpeningHoursRequest;
import com.group1.app.metadata.entity.brand.Brand;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.entity.franchise.FranchiseOpeningHour;
import com.group1.app.metadata.repository.franchise.FranchiseOpeningHourRepository;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseOpeningHourServiceImplTest {

    @InjectMocks
    FranchiseOpeningHourServiceImpl service;

    @Mock
    FranchiseRepository franchiseRepository;

    @Mock
    FranchiseOpeningHourRepository openingHourRepository;

    @Mock
    org.springframework.context.ApplicationEventPublisher publisher;

    @Test
    void updateOpeningHours_franchiseNotFound() {
        UUID id = UUID.randomUUID();
        when(franchiseRepository.findById(id)).thenReturn(Optional.empty());

        UpdateOpeningHoursRequest req = new UpdateOpeningHoursRequest();
        ApiException ex = assertThrows(ApiException.class, () -> service.updateOpeningHours(id, req));
        assertEquals(ErrorCode.FR_404_FRANCHISE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updateOpeningHours_invalidRange() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise();
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        UpdateOpeningHoursRequest req = new UpdateOpeningHoursRequest();
        req.setDayOfWeek(com.group1.app.metadata.enums.DayOfWeekValue.MONDAY);
        req.setOpenTime(LocalTime.of(10, 0));
        req.setCloseTime(LocalTime.of(9, 0));

        ApiException ex = assertThrows(ApiException.class, () -> service.updateOpeningHours(id, req));
        assertEquals(ErrorCode.OH_002_INVALID_TIME_RANGE, ex.getErrorCode());
    }

    @Test
    void updateOpeningHours_exceedsMaxHours() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise();
        Brand b = new Brand();
        b.setMaxOpenMinutesPerDay(60);
        f.setBrand(b);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));

        UpdateOpeningHoursRequest req = new UpdateOpeningHoursRequest();
        req.setDayOfWeek(com.group1.app.metadata.enums.DayOfWeekValue.MONDAY);
        req.setOpenTime(LocalTime.of(8, 0));
        req.setCloseTime(LocalTime.of(12, 0));

        ApiException ex = assertThrows(ApiException.class, () -> service.updateOpeningHours(id, req));
        assertEquals(ErrorCode.OH_003_EXCEEDS_MAX_HOURS, ex.getErrorCode());
    }

    @Test
    void updateOpeningHours_success_createNew() {
        UUID id = UUID.randomUUID();
        Franchise f = new Franchise();
        Brand b = new Brand();
        b.setMaxOpenMinutesPerDay(null);
        f.setBrand(b);
        when(franchiseRepository.findById(id)).thenReturn(Optional.of(f));
        when(openingHourRepository.findByFranchise_IdAndDayOfWeek(id, com.group1.app.metadata.enums.DayOfWeekValue.MONDAY)).thenReturn(Optional.empty());
        when(openingHourRepository.save(org.mockito.ArgumentMatchers.any(FranchiseOpeningHour.class)))
                .thenAnswer(i -> i.getArgument(0));

        UpdateOpeningHoursRequest req = new UpdateOpeningHoursRequest();
        req.setDayOfWeek(com.group1.app.metadata.enums.DayOfWeekValue.MONDAY);
        req.setOpenTime(LocalTime.of(8, 0));
        req.setCloseTime(LocalTime.of(12, 0));

        var resp = service.updateOpeningHours(id, req);
        org.mockito.Mockito.verify(publisher, org.mockito.Mockito.times(1)).publishEvent(org.mockito.ArgumentMatchers.any(com.group1.app.metadata.event.franchise.OpeningHoursUpdatedEvent.class));
        assertNotNull(resp);
        assertEquals(id, resp.getFranchiseId());
    }
}

