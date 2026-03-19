package com.group1.app.metadata.dto.franchisestaff;

import com.fasterxml.jackson.databind.JsonNode;
import com.group1.app.metadata.entity.franchise.Franchise;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FranchiseWithStaffResponse {
    private Franchise franchise;
    private JsonNode staff;
}