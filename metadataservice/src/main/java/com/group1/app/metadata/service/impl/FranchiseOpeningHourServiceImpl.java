package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.event.franchise.OpeningHoursUpdatedEvent;
import com.group1.app.metadata.dto.franchise.request.UpdateOpeningHoursRequest;
import com.group1.app.metadata.dto.franchise.response.OpeningHourResponse;
import com.group1.app.metadata.entity.brand.Brand;
import com.group1.app.metadata.entity.franchise.Franchise;
import com.group1.app.metadata.entity.franchise.FranchiseOpeningHour;
import com.group1.app.metadata.repository.franchise.FranchiseOpeningHourRepository;
import com.group1.app.metadata.repository.franchise.FranchiseRepository;
import com.group1.app.metadata.service.FranchiseOpeningHourService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FranchiseOpeningHourServiceImpl implements FranchiseOpeningHourService {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseOpeningHourRepository openingHourRepository;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public OpeningHourResponse updateOpeningHours(UUID franchiseId, UpdateOpeningHoursRequest request) {

        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ApiException(ErrorCode.FR_404_FRANCHISE_NOT_FOUND));

        if (!request.getCloseTime().isAfter(request.getOpenTime())) {
            throw new ApiException(ErrorCode.OH_002_INVALID_TIME_RANGE);
        }

        Brand brand = franchise.getBrand();

        if (brand == null) {
            throw new ApiException(ErrorCode.OH_001_BRAND_NOT_FOUND);
        }

        long openMinutes = Duration.between(request.getOpenTime(), request.getCloseTime()).toMinutes();

        if (brand.getMaxOpenMinutesPerDay() != null &&
                openMinutes > brand.getMaxOpenMinutesPerDay()) {
            throw new ApiException(ErrorCode.OH_003_EXCEEDS_MAX_HOURS);
        }

        Optional<FranchiseOpeningHour> existing =
                openingHourRepository.findByFranchise_IdAndDayOfWeek(franchiseId, request.getDayOfWeek());

        FranchiseOpeningHour entity = existing.orElseGet(() -> {
            FranchiseOpeningHour h = new FranchiseOpeningHour();
            h.setFranchise(franchise);
            h.setDayOfWeek(request.getDayOfWeek());
            return h;
        });

        entity.setOpenTime(request.getOpenTime());
        entity.setCloseTime(request.getCloseTime());
        entity.setIsClosed(false);

        FranchiseOpeningHour saved = openingHourRepository.save(entity);

        publisher.publishEvent(new OpeningHoursUpdatedEvent(
                franchiseId,
                franchise.getFranchiseCode(),
                saved.getDayOfWeek(),
                saved.getOpenTime(),
                saved.getCloseTime(),
                LocalDateTime.now()
        ));

        return new OpeningHourResponse(
                franchiseId,
                saved.getDayOfWeek(),
                saved.getOpenTime(),
                saved.getCloseTime(),
                saved.getIsClosed()
        );
    }
}
