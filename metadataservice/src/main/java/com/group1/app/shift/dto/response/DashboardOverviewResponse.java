package com.group1.app.shift.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardOverviewResponse {
    int totalShifts;
    int staffOnDuty;
    String coverageRate;
    int pendingCheckIns;
    int absentStaff;
    List<TimelineItemResponse> timeline;
}