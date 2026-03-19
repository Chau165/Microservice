package com.group1.app.shift.dto.response;

import com.group1.app.shift.enums.AttendanceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceResponse {
    String id;
    String shiftId;
    String staffId;
    String staffName;
    AttendanceStatus status;


    Integer lateMinutes;
    Integer earlyLeaveMinutes;

    String markedBy;
    LocalDateTime markedAt;
    LocalDateTime updatedAt;
}