package com.group1.franchiseservice.mapper.contract;

import com.group1.franchiseservice.model.dto.contract.request.CreateContractRequest;
import com.group1.franchiseservice.model.dto.contract.response.CreateContractResponse;
import com.group1.franchiseservice.model.dto.contract.response.RenewContractResponse;
import com.group1.franchiseservice.model.dto.contract.response.TerminateContractResponse;
import com.group1.franchiseservice.model.entity.contract.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractMapper {

    // REQUEST -> ENTITY (Gán mặc định Status là DRAFT và autoOrder là true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "autoOrderEnabled", constant = "true")
    Contract toEntity(CreateContractRequest request);

    // ENTITY -> CREATE RESPONSE
    @Mapping(source = "id", target = "contractId")
    CreateContractResponse toCreateResponse(Contract contract);

    // ENTITY -> RENEW RESPONSE
    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "endDate", target = "newEndDate")
    RenewContractResponse toRenewResponse(Contract contract);

    // ENTITY -> TERMINATE RESPONSE
    @Mapping(source = "id", target = "contractId")
    TerminateContractResponse toTerminateResponse(Contract contract);
}