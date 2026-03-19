package com.group1.app.metadata.service;

import com.group1.app.metadata.dto.franchise.request.UpdateOpeningHoursRequest;
import com.group1.app.metadata.dto.franchise.response.OpeningHourResponse;

import java.util.UUID;

public interface FranchiseOpeningHourService {

    OpeningHourResponse updateOpeningHours(UUID franchiseId, UpdateOpeningHoursRequest request);

}
