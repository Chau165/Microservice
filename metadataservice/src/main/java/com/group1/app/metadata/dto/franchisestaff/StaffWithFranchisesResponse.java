package com.group1.app.metadata.dto.franchisestaff;

import com.group1.app.shift.dto.response.StaffResponse;

import java.util.List;

public record StaffWithFranchisesResponse(

        StaffResponse staff,

        List<FranchiseDTO> franchises

) {}
