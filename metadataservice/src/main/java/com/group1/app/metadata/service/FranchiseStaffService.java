package com.group1.app.metadata.service;

import com.group1.app.metadata.dto.franchisestaff.StaffWithFranchisesResponse;
import com.group1.app.metadata.entity.franchisestaff.FranchiseStaff;

import java.util.List;
import java.util.UUID;

public interface FranchiseStaffService {

    FranchiseStaff assignStaff(UUID franchiseId, String staffId);
    StaffWithFranchisesResponse getFranchiseByStaffId(String staffId);
    List<FranchiseStaff> getAllByFranchiseId(UUID franchiseId);
    void removeStaff(UUID franchiseId, String staffId);
}