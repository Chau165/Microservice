package com.group1.app.common.util;

import java.util.Map;

public final class ContractSortFields {

    private ContractSortFields(){}

    public static final Map<String,String> FIELDS = Map.ofEntries(
            Map.entry("contractid","id"),
            Map.entry("contractnumber","contractNumber"),
            Map.entry("franchiseid","franchise.id"),
            Map.entry("franchisecode","franchise.franchiseCode"),
            Map.entry("status","status"),
            Map.entry("startdate","startDate"),
            Map.entry("enddate","endDate"),
            Map.entry("createdby","createdBy"),
            Map.entry("createdat","createdAt"),
            Map.entry("activatedat","activatedAt"),
            Map.entry("activatedby","activatedBy")
    );

}
