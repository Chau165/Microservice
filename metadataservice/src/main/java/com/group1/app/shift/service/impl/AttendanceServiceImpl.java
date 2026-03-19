package com.group1.app.shift.service.impl;

import com.group1.app.shift.dto.request.AttendanceItemRequest;
import com.group1.app.shift.dto.request.BulkMarkAttendanceRequest;
import com.group1.app.shift.dto.response.AttendanceReportResponse;
import com.group1.app.shift.dto.response.AttendanceResponse;
import com.group1.app.shift.dto.response.DashboardOverviewResponse;
import com.group1.app.shift.dto.response.StaffAttendanceDetailsResponse;
import com.group1.app.shift.dto.response.TimelineItemResponse;
import com.group1.app.shift.entity.Attendance;
import com.group1.app.shift.entity.Shift;
import com.group1.app.shift.entity.ShiftAssignment;
import com.group1.app.shift.entity.Staff;
import com.group1.app.shift.enums.AttendanceStatus;
import com.group1.app.shift.enums.ScheduleStatus;
import com.group1.app.shift.exception.AppException;
import com.group1.app.shift.exception.ErrorCode;
import com.group1.app.shift.repository.AttendanceRepository;
import com.group1.app.shift.repository.ShiftAssignmentRepository;
import com.group1.app.shift.repository.ShiftRepository;
import com.group1.app.shift.repository.StaffRepository;
import com.group1.app.shift.service.AttendanceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceServiceImpl implements AttendanceService {

    AttendanceRepository attendanceRepository;
    ShiftRepository shiftRepository;
    ShiftAssignmentRepository shiftAssignmentRepository;
    StaffRepository staffRepository;

    @Override
    @Transactional
    public List<AttendanceResponse> bulkMarkAttendance(String shiftId, BulkMarkAttendanceRequest request, String markedBy) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_FOUND));

        Set<String> assignedIds = shiftAssignmentRepository.findAllByShiftId(shiftId)
                .stream().map(ShiftAssignment::getStaffId).collect(Collectors.toSet());

        List<String> requestedStaffIds = request.getAttendances().stream()
                .map(AttendanceItemRequest::getStaffId).distinct().collect(Collectors.toList());

        Map<String, String> staffNameMap = staffRepository.findAllById(requestedStaffIds)
                .stream().collect(Collectors.toMap(Staff::getId, Staff::getName));

        for (AttendanceItemRequest item : request.getAttendances()) {
            if (!assignedIds.contains(item.getStaffId())) {
                throw new AppException(ErrorCode.STAFF_NOT_IN_SHIFT, item.getStaffId());
            }
        }

        Map<String, Attendance> existingMap = attendanceRepository.findAllByShiftId(shiftId)
                .stream().collect(Collectors.toMap(Attendance::getStaffId, a -> a));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        List<Attendance> toSave = request.getAttendances().stream().map(item -> {
            Attendance existing = existingMap.get(item.getStaffId());
            AttendanceStatus actualStatus = item.getStatus();

            Integer lateMins  = existing != null ? existing.getLateMinutes()       : null;
            Integer earlyMins = existing != null ? existing.getEarlyLeaveMinutes() : null;

            if (item.getStatus() == AttendanceStatus.PRESENT) {
                LocalDateTime shiftStart = LocalDateTime.of(shift.getDate(), shift.getStartTime());
                long diff = Duration.between(shiftStart, now).toMinutes();
                lateMins     = (int) Math.max(0, diff);
                actualStatus = lateMins > 0 ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
                earlyMins    = 0;
            } else if (item.getStatus() == AttendanceStatus.EARLY_LEAVE) {
                LocalDateTime shiftEnd = LocalDateTime.of(shift.getDate(), shift.getEndTime());
                long diff = Duration.between(now, shiftEnd).toMinutes();
                earlyMins = (int) Math.max(0, diff);
                if (earlyMins > 0) {
                    actualStatus = AttendanceStatus.EARLY_LEAVE;
                } else {
                    actualStatus = (existing != null && existing.getStatus() == AttendanceStatus.LATE)
                            ? AttendanceStatus.LATE
                            : AttendanceStatus.PRESENT;
                }
            } else if (item.getStatus() == AttendanceStatus.ABSENT) {
                actualStatus = AttendanceStatus.ABSENT;
                lateMins  = 0;
                earlyMins = 0;
            }

            if (existing != null) {
                existing.setStatus(actualStatus);
                existing.setLateMinutes(lateMins);
                existing.setEarlyLeaveMinutes(earlyMins);
                existing.setUpdatedBy(markedBy);
                return existing;
            } else {
                return Attendance.builder()
                        .shiftId(shiftId).staffId(item.getStaffId()).status(actualStatus)
                        .lateMinutes(lateMins).earlyLeaveMinutes(earlyMins).markedBy(markedBy).build();
            }
        }).collect(Collectors.toList());

        // save attendances
        List<Attendance> saved = attendanceRepository.saveAll(toSave);

        // update related shift assignments status according to attendance
        List<ShiftAssignment> assignmentsToUpdate = saved.stream().map(a -> {
            ShiftAssignment asg = shiftAssignmentRepository.findByShiftIdAndStaffId(a.getShiftId(), a.getStaffId()).orElse(null);
            if (asg == null) return null;
            if (a.getStatus() == AttendanceStatus.ABSENT) {
                asg.setStatus(ScheduleStatus.CANCELED);
            } else {
                // PRESENT, LATE, EARLY_LEAVE -> treat as present
                asg.setStatus(ScheduleStatus.COMPLETED
                );
            }
            return asg;
        }).filter(x -> x != null).collect(Collectors.toList());

        if (!assignmentsToUpdate.isEmpty()) {
            shiftAssignmentRepository.saveAll(assignmentsToUpdate);
        }

        return saved.stream()
                .map(a -> toResponse(a, staffNameMap.get(a.getStaffId()))).collect(Collectors.toList());
    }

    @Override
    public AttendanceResponse updateAttendance(String attendanceId, AttendanceItemRequest request, String updatedBy) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByShift(String shiftId) {
        if (!shiftRepository.existsById(shiftId)) throw new AppException(ErrorCode.SHIFT_NOT_FOUND);
        List<Attendance> list = attendanceRepository.findAllByShiftId(shiftId);
        Map<String, String> staffNameMap = staffRepository
                .findAllById(list.stream().map(Attendance::getStaffId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Staff::getId, Staff::getName));
        return list.stream()
                .map(a -> toResponse(a, staffNameMap.getOrDefault(a.getStaffId(), "Unknown")))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceReportResponse> getAttendanceReport(int month, int year) {
        List<Staff> staffs = staffRepository.findAll();

        List<Shift> allShifts = shiftRepository.findAll().stream()
                .filter(s -> s.getDate().getMonthValue() == month && s.getDate().getYear() == year)
                .collect(Collectors.toList());

        List<ShiftAssignment> assignments = shiftAssignmentRepository.findAll();
        List<Attendance> attendances      = attendanceRepository.findAll();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        return staffs.stream().map(staff -> {
                    int totalAssignedMins = 0, penaltyMins = 0;
                    int presentCount = 0, absentCount = 0;
                    int totalLateMins = 0, totalEarlyMins = 0;
                    int validShiftsCount = 0;

                    List<Shift> staffShifts = assignments.stream()
                            .filter(a -> a.getStaffId().equals(staff.getId()))
                            .map(a -> allShifts.stream().filter(s -> s.getId().equals(a.getShiftId())).findFirst().orElse(null))
                            .filter(s -> s != null).collect(Collectors.toList());

                    for (Shift shift : staffShifts) {
                        LocalDateTime shiftStart = LocalDateTime.of(shift.getDate(), shift.getStartTime());
                        if (!now.isBefore(shiftStart)) {
                            validShiftsCount++;
                            int shiftDuration = (int) Duration.between(shift.getStartTime(), shift.getEndTime()).toMinutes();
                            totalAssignedMins += shiftDuration;

                            Attendance record = attendances.stream()
                                    .filter(a -> a.getShiftId().equals(shift.getId()) && a.getStaffId().equals(staff.getId()))
                                    .findFirst().orElse(null);

                            if (record != null) {
                                if (record.getStatus() == AttendanceStatus.ABSENT) {
                                    absentCount++;
                                    penaltyMins += shiftDuration;
                                } else {
                                    presentCount++;
                                    int recLateMins  = record.getLateMinutes()       != null ? record.getLateMinutes()       : 0;
                                    int recEarlyMins = record.getEarlyLeaveMinutes() != null ? record.getEarlyLeaveMinutes() : 0;
                                    totalLateMins  += recLateMins;
                                    totalEarlyMins += recEarlyMins;
                                    penaltyMins    += recLateMins + recEarlyMins;
                                }
                            } else {
                                LocalDateTime shiftEnd = LocalDateTime.of(shift.getDate(), shift.getEndTime()).plusMinutes(30);
                                if (now.isAfter(shiftEnd)) {
                                    absentCount++;
                                    penaltyMins += shiftDuration;
                                }
                            }
                        }
                    }

                    double coverage = 0.0;
                    if (totalAssignedMins > 0) {
                        int workedMins = Math.max(0, totalAssignedMins - penaltyMins);
                        coverage = Math.round(((double) workedMins / totalAssignedMins) * 100.0);
                    }

                    return AttendanceReportResponse.builder()
                            .staffId(staff.getId())
                            .staffCode(staff.getStaffCode() != null ? staff.getStaffCode() : "N/A")
                            .staffName(staff.getName())
                            .assignedShifts(validShiftsCount)
                            .presentCount(presentCount)
                            .absentCount(absentCount)
                            .totalLateMins(totalLateMins)
                            .totalEarlyMins(totalEarlyMins)
                            .coveragePercentage(coverage)
                            .build();
                }).sorted((a, b) -> Double.compare(b.getCoveragePercentage(), a.getCoveragePercentage()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardOverviewResponse getDashboardOverview(LocalDate date) {
        List<Shift> shifts = shiftRepository.findAllByDate(date);

        int totalAssigned = 0, presentCount = 0, absentCount = 0, pendingCount = 0;
        List<TimelineItemResponse> timeline = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        for (Shift shift : shifts) {
            List<ShiftAssignment> assignments = shiftAssignmentRepository.findAllByShiftId(shift.getId());
            List<Attendance> attendances      = attendanceRepository.findAllByShiftId(shift.getId());

            int shiftAssignedCount = assignments.size();
            totalAssigned += shiftAssignedCount;
            int shiftPresentCount = 0;

            for (ShiftAssignment assignment : assignments) {
                Attendance record = attendances.stream()
                        .filter(a -> a.getStaffId().equals(assignment.getStaffId()))
                        .findFirst().orElse(null);

                if (record != null) {
                    if (record.getStatus() == AttendanceStatus.PRESENT
                            || record.getStatus() == AttendanceStatus.LATE
                            || record.getStatus() == AttendanceStatus.EARLY_LEAVE) {
                        presentCount++; shiftPresentCount++;
                    } else if (record.getStatus() == AttendanceStatus.ABSENT) {
                        absentCount++;
                    } else {
                        pendingCount++;
                    }
                } else {
                    pendingCount++;
                }
            }

            boolean isFull  = (shiftPresentCount == shiftAssignedCount) && (shiftAssignedCount > 0);
            String timeStr  = shift.getStartTime().toString().substring(0, 5) + " - " + shift.getEndTime().toString().substring(0, 5);
            String currentStatus = calculateShiftStatus(shift, now);

            timeline.add(TimelineItemResponse.builder()
                    .id(shift.getId())
                    .shiftName("Ca làm việc (" + currentStatus + ")")
                    .time(timeStr)
                    .presentStaff(shiftPresentCount)
                    .assignedStaff(shiftAssignedCount)
                    .status(isFull ? "FULL" : "MISSING")
                    .branchId(shift.getBranchId())
                    .build());
        }

        timeline.sort((a, b) -> a.getTime().compareTo(b.getTime()));
        int coverage = totalAssigned > 0 ? Math.round(((float) presentCount / totalAssigned) * 100) : 0;

        return DashboardOverviewResponse.builder()
                .totalShifts(shifts.size())
                .staffOnDuty(presentCount)
                .coverageRate(coverage + "%")
                .pendingCheckIns(pendingCount)
                .absentStaff(absentCount)
                .timeline(timeline)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffAttendanceDetailsResponse> getStaffAttendanceHistory(String staffId, Integer month, Integer year, LocalDate exactDate) {
        List<String> assignedShiftIds = shiftAssignmentRepository.findAll().stream()
                .filter(a -> a.getStaffId().equals(staffId))
                .map(ShiftAssignment::getShiftId)
                .collect(Collectors.toList());

        List<Shift> shifts = shiftRepository.findAllById(assignedShiftIds);

        if (exactDate != null) {
            shifts = shifts.stream()
                    .filter(s -> s.getDate().equals(exactDate))
                    .collect(Collectors.toList());
        } else if (month != null && year != null) {
            shifts = shifts.stream()
                    .filter(s -> s.getDate().getMonthValue() == month && s.getDate().getYear() == year)
                    .collect(Collectors.toList());
        }

        Map<String, Attendance> attMap = attendanceRepository.findAll().stream()
                .filter(a -> a.getStaffId().equals(staffId))
                .collect(Collectors.toMap(Attendance::getShiftId, a -> a));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        return shifts.stream().map(shift -> {
                    Attendance record = attMap.get(shift.getId());
                    String attStatus = "UNMARKED";
                    Integer lateMins = 0;
                    Integer earlyMins = 0;

                    if (record != null) {
                        attStatus = record.getStatus().name();
                        lateMins  = record.getLateMinutes() != null ? record.getLateMinutes()       : 0;
                        earlyMins = record.getEarlyLeaveMinutes() != null ? record.getEarlyLeaveMinutes() : 0;
                    } else {
                        LocalDateTime shiftEnd = LocalDateTime.of(shift.getDate(), shift.getEndTime()).plusMinutes(30);
                        if (now.isAfter(shiftEnd)) attStatus = "ABSENT";
                    }

                    return StaffAttendanceDetailsResponse.builder()
                            .shiftId(shift.getId())
                            .date(shift.getDate())
                            .startTime(shift.getStartTime())
                            .endTime(shift.getEndTime())
                            .branchId(shift.getBranchId())
                            .shiftStatus(calculateShiftStatus(shift, now))
                            .attendanceStatus(attStatus)
                            .lateMinutes(lateMins)
                            .earlyLeaveMinutes(earlyMins)
                            .build();
                }).sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .collect(Collectors.toList());
    }

    private String calculateShiftStatus(Shift shift, LocalDateTime now) {
        LocalDateTime shiftStart = LocalDateTime.of(shift.getDate(), shift.getStartTime());
        LocalDateTime shiftEnd = LocalDateTime.of(shift.getDate(), shift.getEndTime());
        LocalDateTime allowCheckInTime = shiftStart.minusMinutes(30);
        LocalDateTime closeTime = shiftEnd.plusMinutes(30);

        if (now.isBefore(allowCheckInTime)) return "PREPARING";
        else if (now.isAfter(closeTime)) return "CLOSED";
        else return "OPEN";
    }

    private AttendanceResponse toResponse(Attendance a, String staffName) {
        return AttendanceResponse.builder()
                .id(a.getId()).shiftId(a.getShiftId()).staffId(a.getStaffId())
                .staffName(staffName).status(a.getStatus())
                .lateMinutes(a.getLateMinutes()).earlyLeaveMinutes(a.getEarlyLeaveMinutes())
                .markedBy(a.getMarkedBy()).markedAt(a.getMarkedAt()).updatedAt(a.getUpdatedAt())
                .build();
    }
}

