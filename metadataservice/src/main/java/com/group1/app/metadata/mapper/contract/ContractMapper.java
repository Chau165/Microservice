package com.group1.app.metadata.mapper.contract;

import com.group1.app.metadata.dto.contract.request.CreateContractRequest;
import com.group1.app.metadata.dto.contract.response.*;
import com.group1.app.metadata.entity.contract.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "autoOrderEnabled", ignore = true)
    Contract toEntity(CreateContractRequest request);

    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "status", target = "status")
    CreateContractResponse toCreateResponse(Contract contract);

    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "endDate", target = "newEndDate")
    @Mapping(target = "status", expression = "java(contract.getEffectiveStatus())")
    @Mapping(source = "renewedAt", target = "renewedAt")
    @Mapping(source = "renewedBy", target = "renewedBy")
    RenewContractResponse toRenewResponse(Contract contract);

    @Mapping(source = "id", target = "contractId")
    @Mapping(target = "status", expression = "java(contract.getEffectiveStatus())")
    @Mapping(source = "terminatedAt", target = "terminatedAt")
    @Mapping(source = "terminatedBy", target = "terminatedBy")
    @Mapping(source = "terminationReason", target = "terminationReason")
    TerminateContractResponse toTerminateResponse(Contract contract);

    // Dành cho List (Get All / Search)
    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "franchise.id", target = "franchiseId")
    @Mapping(source = "franchise.franchiseCode", target = "franchiseCode")
    @Mapping(target = "status", expression = "java(contract.getEffectiveStatus())")
    @Mapping(source = "royaltyRate", target = "royaltyRate")
    @Mapping(source = "autoOrderEnabled", target = "autoOrderEnabled")
    ContractListResponse toListResponse(Contract contract);

    // Dành cho Detail (Get By Id) - Tự động map thêm royaltyRate, autoOrderEnabled, v.v.
    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "franchise.id", target = "franchiseId")
    @Mapping(source = "franchise.franchiseCode", target = "franchiseCode")
    @Mapping(target = "status", expression = "java(contract.getEffectiveStatus())")
    ContractResponse toDetailResponse(Contract contract);
}