package com.group1.app.metadata.dto.franchise.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FranchiseConfigurationResponse {

    private boolean featureFlags;

    private List<OpeningHourResponse> openingHours;

    private boolean menuProfileAssigned;

    private boolean warehouseMappingConfigured;

    private boolean posEnabled;

    private boolean orderingEnabled;

    private boolean autoOrderEnabled;

}
