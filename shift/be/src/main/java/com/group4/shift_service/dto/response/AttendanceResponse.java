package com.group4.shift_service.dto.response;

import com.group4.shift_service.enums.AttendanceStatus;
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