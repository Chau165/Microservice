package com.group1.app.shift.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimelineItemResponse {
    String id;
    String shiftName;
    String time;


    int presentStaff;
    int assignedStaff;

    String status;
    String branchId;
}