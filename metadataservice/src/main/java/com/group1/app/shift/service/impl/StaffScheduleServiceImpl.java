package com.group1.app.shift.service.impl;

import com.group1.app.shift.dto.request.StaffScheduleRequest;
import com.group1.app.shift.dto.response.StaffScheduleResponse;

import com.group1.app.shift.dto.response.StaffScheduleWithAttendanceResponse;
import com.group1.app.shift.entity.ShiftAssignment;
import com.group1.app.shift.entity.Attendance;
import com.group1.app.shift.exception.AppException;
import com.group1.app.shift.exception.ErrorCode;
import com.group1.app.shift.repository.ShiftAssignmentRepository;
import com.group1.app.shift.repository.StaffRepository;
import com.group1.app.shift.repository.AttendanceRepository;
import com.group1.app.shift.service.StaffScheduleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffScheduleServiceImpl implements StaffScheduleService {

    ShiftAssignmentRepository shiftAssignmentRepository;
    StaffRepository staffRepository;
    AttendanceRepository attendanceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StaffScheduleResponse> getSchedulesByStaffId(String staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new AppException(ErrorCode.STAFF_NOT_FOUND, staffId);
        }

        List<ShiftAssignment> assignments = shiftAssignmentRepository.findAllByStaffId(staffId);
        return assignments.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public StaffScheduleResponse createSchedule(StaffScheduleRequest request) {
        if (!staffRepository.existsById(request.getStaffId())) {
            throw new AppException(ErrorCode.STAFF_NOT_FOUND, request.getStaffId());
        }

        ShiftAssignment a = ShiftAssignment.builder()
                .id(UUID.randomUUID().toString())
                .staffId(request.getStaffId())
                .shiftId(request.getShiftId())
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .branchId(request.getBranchId())
                .build();

        ShiftAssignment saved = shiftAssignmentRepository.save(a);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public StaffScheduleResponse updateSchedule(String scheduleId, StaffScheduleRequest request) {
        ShiftAssignment a = shiftAssignmentRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_NOT_FOUND));

        a.setDate(request.getDate());
        a.setStartTime(request.getStartTime());
        a.setEndTime(request.getEndTime());
        a.setBranchId(request.getBranchId());

        return mapToResponse(shiftAssignmentRepository.save(a));
    }

    @Override
    public void deleteSchedule(String scheduleId) {
        if (!shiftAssignmentRepository.existsById(scheduleId)) {
            throw new AppException(ErrorCode.ATTENDANCE_NOT_FOUND);
        }
        shiftAssignmentRepository.deleteById(scheduleId);
    }

    // New method: compute schedule list with attendance-derived status
    @Override
    @Transactional(readOnly = true)
    public List<StaffScheduleWithAttendanceResponse> getSchedulesWithAttendance(String staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new AppException(ErrorCode.STAFF_NOT_FOUND, staffId);
        }

        List<ShiftAssignment> assignments = shiftAssignmentRepository.findAllByStaffId(staffId);
        // load attendances for this staff
        Map<String, Attendance> attMap = attendanceRepository.findAll().stream()
                .filter(a -> a.getStaffId().equals(staffId))
                .collect(Collectors.toMap(Attendance::getShiftId, a -> a));


        return assignments.stream().map(a -> {
            Attendance rec = attMap.get(a.getShiftId());
            String status;
            if (rec == null) {
                status = "SCHEDULED";
            } else if (rec.getStatus() != null && rec.getStatus().name().equals("ABSENT")) {
                status = "ABSENT";
            } else {
                // Attendance entity in this project doesn't store check-in/out timestamps, so derive using status and markedAt
                // If attendance exists and status not ABSENT -> treat as COMPLETED
                // But to satisfy IN_PROGRESS rule, check if markedAt exists and updatedAt is null (best-effort)
                if (rec.getMarkedAt() != null && rec.getUpdatedAt() == null) {
                    status = "IN_PROGRESS";
                } else if (rec.getMarkedAt() != null) {
                    status = "COMPLETED";
                } else {
                    status = "SCHEDULED";
                }
            }

            return StaffScheduleWithAttendanceResponse.builder()
                    .id(a.getId())
                    .staffId(a.getStaffId())
                    .shiftId(a.getShiftId())
                    .date(a.getDate())
                    .startTime(a.getStartTime())
                    .endTime(a.getEndTime())
                    .branchId(a.getBranchId())
                    .status(status)
                    .build();
        }).sorted((x, y) -> y.getDate().compareTo(x.getDate())).collect(Collectors.toList());
    }

    private StaffScheduleResponse mapToResponse(ShiftAssignment a) {
        // If there is an attendance record for this assignment that marks ABSENT, prefer CANCELED
        var opt = attendanceRepository.findByShiftIdAndStaffId(a.getShiftId(), a.getStaffId());
        if (opt.isPresent() && opt.get().getStatus() != null && opt.get().getStatus().name().equals("ABSENT")) {
            return StaffScheduleResponse.builder()
                    .id(a.getId())
                    .staffId(a.getStaffId())
                    .shiftId(a.getShiftId())
                    .date(a.getDate())
                    .startTime(a.getStartTime())
                    .endTime(a.getEndTime())
                    .branchId(a.getBranchId())
                    .status(com.group1.app.shift.enums.ScheduleStatus.CANCELED)
                    .build();
        }

        return StaffScheduleResponse.builder()
                .id(a.getId())
                .staffId(a.getStaffId())
                .shiftId(a.getShiftId())
                .date(a.getDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .branchId(a.getBranchId())
                .status(a.getStatus())
                .build();
    }
}
