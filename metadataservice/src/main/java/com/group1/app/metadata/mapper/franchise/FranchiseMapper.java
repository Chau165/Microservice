package com.group1.app.metadata.mapper.franchise;

import com.group1.app.metadata.dto.franchise.request.CreateFranchiseRequest;
import com.group1.app.metadata.dto.franchise.request.UpdateFranchiseRequest;
import com.group1.app.metadata.dto.franchise.response.FranchiseResponse;
import com.group1.app.metadata.entity.franchise.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FranchiseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "onboardingStatus", constant = "PENDING")
    @Mapping(target = "featureFlags", constant = "false")
    Franchise toEntity(CreateFranchiseRequest request);

    @Mapping(source = "id", target = "franchiseId")
    FranchiseResponse toResponse(Franchise franchise);

    // Update identity fields (franchiseCode, status and onboardingStatus won't be changed here)
    @Mapping(target = "franchiseCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "onboardingStatus", ignore = true)
    @Mapping(target = "featureFlags", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    Franchise toEntity(UpdateFranchiseRequest request);
}

