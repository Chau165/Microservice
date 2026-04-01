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

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        LocalDateTime shiftStart = getShiftStart(shift);
        LocalDateTime shiftEnd = getShiftEnd(shift);
        LocalDateTime allowCheckInTime = shiftStart.minusMinutes(30);
        LocalDateTime closeTime = shiftEnd.plusMinutes(30);

        // Chỉ cho phép điểm danh trong khoảng mở -> đóng
        if (now.isBefore(allowCheckInTime)) {
            throw new AppException(ErrorCode.ATTENDANCE_NOT_OPEN_YET);
        }

        if (now.isAfter(closeTime)) {
            throw new AppException(ErrorCode.ATTENDANCE_MARKING_CLOSED);
        }

        Set<String> assignedIds = shiftAssignmentRepository.findAllByShiftId(shiftId)
                .stream()
                .map(ShiftAssignment::getStaffId)
                .collect(Collectors.toSet());

        List<String> requestedStaffIds = request.getAttendances().stream()
                .map(AttendanceItemRequest::getStaffId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> staffNameMap = staffRepository.findAllById(requestedStaffIds)
                .stream()
                .collect(Collectors.toMap(Staff::getId, Staff::getName));

        for (AttendanceItemRequest item : request.getAttendances()) {
            if (!assignedIds.contains(item.getStaffId())) {
                throw new AppException(ErrorCode.STAFF_NOT_IN_SHIFT, item.getStaffId());
            }
        }

        Map<String, Attendance> existingMap = attendanceRepository.findAllByShiftId(shiftId)
                .stream()
                .collect(Collectors.toMap(Attendance::getStaffId, a -> a));

        List<Attendance> toSave = request.getAttendances().stream().map(item -> {
            Attendance existing = existingMap.get(item.getStaffId());

            AttendanceStatus actualStatus = item.getStatus();
            Integer lateMins = existing != null ? safeInt(existing.getLateMinutes()) : 0;
            Integer earlyMins = existing != null ? safeInt(existing.getEarlyLeaveMinutes()) : 0;

            // ===== CHECK-IN =====
            if (item.getStatus() == AttendanceStatus.PRESENT) {
                long diff = Duration.between(shiftStart, now).toMinutes();
                lateMins = (int) Math.max(0, diff);
                actualStatus = lateMins > 0 ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
                earlyMins = 0;
            }

            // ===== CHECK-OUT =====
            else if (item.getStatus() == AttendanceStatus.EARLY_LEAVE) {
                // Nếu checkout trước khi shift bắt đầu -> tính ABSENT
                if (now.isBefore(shiftStart)) {
                    actualStatus = AttendanceStatus.ABSENT;
                    lateMins = 0;
                    earlyMins = 0;
                } else {
                    // Không cho checkout nếu chưa từng checkin
                    if (existing == null || existing.getStatus() == AttendanceStatus.ABSENT) {
                        throw new AppException(ErrorCode.CANNOT_CHECKOUT_BEFORE_CHECKIN, item.getStaffId());
                    }

                    // Nếu record trước đó chưa từng là check-in hợp lệ thì cũng không cho checkout
                    if (!(existing.getStatus() == AttendanceStatus.PRESENT
                            || existing.getStatus() == AttendanceStatus.LATE
                            || existing.getStatus() == AttendanceStatus.EARLY_LEAVE)) {
                        throw new AppException(ErrorCode.CANNOT_CHECKOUT_BEFORE_CHECKIN, item.getStaffId());
                    }

                    long diff = Duration.between(now, shiftEnd).toMinutes();
                    earlyMins = (int) Math.max(0, diff);

                    if (earlyMins > 0) {
                        actualStatus = AttendanceStatus.EARLY_LEAVE;
                    } else {
                        // Nếu trước đó đã từng đi trễ thì vẫn giữ LATE
                        actualStatus = (existing.getStatus() == AttendanceStatus.LATE)
                                ? AttendanceStatus.LATE
                                : AttendanceStatus.PRESENT;
                        earlyMins = 0;
                    }

                    // Giữ nguyên lateMinutes từ lần check-in trước
                    lateMins = safeInt(existing.getLateMinutes());
                }
            }

            // ===== ABSENT =====
            else if (item.getStatus() == AttendanceStatus.ABSENT) {
                actualStatus = AttendanceStatus.ABSENT;
                lateMins = 0;
                earlyMins = 0;
            }

            if (existing != null) {
                existing.setStatus(actualStatus);
                existing.setLateMinutes(lateMins);
                existing.setEarlyLeaveMinutes(earlyMins);
                existing.setUpdatedBy(markedBy); // nếu entity bạn không có field này thì xóa dòng này
                return existing;
            } else {
                return Attendance.builder()
                        .shiftId(shiftId)
                        .staffId(item.getStaffId())
                        .status(actualStatus)
                        .lateMinutes(lateMins)
                        .earlyLeaveMinutes(earlyMins)
                        .markedBy(markedBy)
                        .build();
            }
        }).collect(Collectors.toList());

        List<Attendance> saved = attendanceRepository.saveAll(toSave);

        List<ShiftAssignment> assignmentsToUpdate = saved.stream().map(a -> {
            ShiftAssignment asg = shiftAssignmentRepository
                    .findByShiftIdAndStaffId(a.getShiftId(), a.getStaffId())
                    .orElse(null);

            if (asg == null) return null;

            if (a.getStatus() == AttendanceStatus.ABSENT) {
                asg.setStatus(ScheduleStatus.CANCELED);
            } else {
                asg.setStatus(ScheduleStatus.COMPLETED);
            }
            return asg;
        }).filter(x -> x != null).collect(Collectors.toList());

        if (!assignmentsToUpdate.isEmpty()) {
            shiftAssignmentRepository.saveAll(assignmentsToUpdate);
        }

        return saved.stream()
                .map(a -> toResponse(a, staffNameMap.getOrDefault(a.getStaffId(), "Unknown")))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(String attendanceId, AttendanceItemRequest request, String updatedBy) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_NOT_FOUND));

        Shift shift = shiftRepository.findById(attendance.getShiftId())
                .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime shiftStart = getShiftStart(shift);
        LocalDateTime shiftEnd = getShiftEnd(shift);
        LocalDateTime allowCheckInTime = shiftStart.minusMinutes(30);
        LocalDateTime closeTime = shiftEnd.plusMinutes(30);

        if (now.isBefore(allowCheckInTime)) {
            throw new AppException(ErrorCode.ATTENDANCE_NOT_OPEN_YET);
        }

        if (now.isAfter(closeTime)) {
            throw new AppException(ErrorCode.ATTENDANCE_MARKING_CLOSED);
        }

        AttendanceStatus actualStatus = request.getStatus();
        Integer lateMins = safeInt(attendance.getLateMinutes());
        Integer earlyMins = safeInt(attendance.getEarlyLeaveMinutes());

        if (request.getStatus() == AttendanceStatus.PRESENT) {
            long diff = Duration.between(shiftStart, now).toMinutes();
            lateMins = (int) Math.max(0, diff);
            actualStatus = lateMins > 0 ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
            earlyMins = 0;
        } else if (request.getStatus() == AttendanceStatus.EARLY_LEAVE) {
            // Nếu checkout trước khi shift bắt đầu -> tính ABSENT
            if (now.isBefore(shiftStart)) {
                actualStatus = AttendanceStatus.ABSENT;
                lateMins = 0;
                earlyMins = 0;
            } else {
                if (!(attendance.getStatus() == AttendanceStatus.PRESENT
                        || attendance.getStatus() == AttendanceStatus.LATE
                        || attendance.getStatus() == AttendanceStatus.EARLY_LEAVE)) {
                    throw new AppException(ErrorCode.CANNOT_CHECKOUT_BEFORE_CHECKIN, attendance.getStaffId());
                }

                long diff = Duration.between(now, shiftEnd).toMinutes();
                earlyMins = (int) Math.max(0, diff);

                if (earlyMins > 0) {
                    actualStatus = AttendanceStatus.EARLY_LEAVE;
                } else {
                    actualStatus = (attendance.getStatus() == AttendanceStatus.LATE)
                            ? AttendanceStatus.LATE
                            : AttendanceStatus.PRESENT;
                    earlyMins = 0;
                }
            }
        } else if (request.getStatus() == AttendanceStatus.ABSENT) {
            actualStatus = AttendanceStatus.ABSENT;
            lateMins = 0;
            earlyMins = 0;
        }

        attendance.setStatus(actualStatus);
        attendance.setLateMinutes(lateMins);
        attendance.setEarlyLeaveMinutes(earlyMins);
        attendance.setUpdatedBy(updatedBy); // nếu entity không có field này thì xóa dòng này

        Attendance saved = attendanceRepository.save(attendance);

        String staffName = staffRepository.findById(saved.getStaffId())
                .map(Staff::getName)
                .orElse("Unknown");

        ShiftAssignment asg = shiftAssignmentRepository
                .findByShiftIdAndStaffId(saved.getShiftId(), saved.getStaffId())
                .orElse(null);

        if (asg != null) {
            if (saved.getStatus() == AttendanceStatus.ABSENT) {
                asg.setStatus(ScheduleStatus.CANCELED);
            } else {
                asg.setStatus(ScheduleStatus.COMPLETED);
            }
            shiftAssignmentRepository.save(asg);
        }

        return toResponse(saved, staffName);
    }

    // ... các phần trước giữ nguyên ...

    @Override
    @Transactional
    public List<AttendanceResponse> getAttendanceByShift(String shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_FOUND));

        List<Attendance> list = new ArrayList<>(attendanceRepository.findAllByShiftId(shiftId));
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime shiftEnd = getShiftEnd(shift);
        LocalDateTime autoClosingTime = shiftEnd.plusMinutes(30);

        // LOGIC MỚI: Xử lý dữ liệu khi ca đã kết thúc (CLOSED)
        if (now.isAfter(autoClosingTime)) {
            // 1. Tự động đánh vắng cho những người KHÔNG check-in (Giữ nguyên logic cũ của bạn)
            Set<String> existingStaffIds = list.stream().map(Attendance::getStaffId).collect(Collectors.toSet());
            List<ShiftAssignment> assignments = shiftAssignmentRepository.findAllByShiftId(shiftId);

            List<Attendance> autoAbsentList = assignments.stream()
                    .filter(a -> !existingStaffIds.contains(a.getStaffId()))
                    .map(a -> Attendance.builder()
                            .shiftId(shiftId).staffId(a.getStaffId()).status(AttendanceStatus.ABSENT)
                            .lateMinutes(0).earlyLeaveMinutes(0).markedBy("SYSTEM_AUTO").build())
                    .collect(Collectors.toList());

            if (!autoAbsentList.isEmpty()) {
                // Lưu ABSENT vào database
                List<Attendance> savedAbsent = attendanceRepository.saveAll(autoAbsentList);
                list.addAll(savedAbsent);

                // Cập nhật trạng thái assignment thành CANCELED cho người vắng
                assignments.stream()
                        .filter(a -> autoAbsentList.stream().anyMatch(x -> x.getStaffId().equals(a.getStaffId())))
                        .forEach(a -> {
                            a.setStatus(ScheduleStatus.CANCELED);
                            shiftAssignmentRepository.save(a);
                        });
            }

            // 2. LOGIC QUÊN CHECK-OUT: Đối với những người ĐÃ check-in nhưng ca đã đóng
            // Chúng ta sẽ map lại response để hiển thị là hoàn thành (không tính về sớm)
        }

        Map<String, String> staffNameMap = staffRepository.findAllById(list.stream().map(Attendance::getStaffId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Staff::getId, Staff::getName));

        return list.stream()
                .map(a -> {
                    AttendanceResponse res = toResponse(a, staffNameMap.getOrDefault(a.getStaffId(), "Unknown"));

                    // Nếu ca đã đóng mà trạng thái vẫn là chỉ mới vào (PRESENT/LATE) và chưa có EarlyMins
                    // Thì set earlyLeaveMinutes = 0 để tính là về đúng giờ
                    if (now.isAfter(autoClosingTime) &&
                            (a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)) {
                        res.setEarlyLeaveMinutes(0);
                    }
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceReportResponse> getAttendanceReport(int month, int year, String branchId) {
        List<Staff> staffs = staffRepository.findAll().stream()
                .filter(s -> branchId == null || branchId.trim().isEmpty() || branchId.equals(s.getBranchId()))
                .collect(Collectors.toList());

        List<Shift> allShifts = shiftRepository.findAll().stream()
                .filter(s -> s.getDate().getMonthValue() == month && s.getDate().getYear() == year)
                .filter(s -> branchId == null || branchId.trim().isEmpty() || branchId.equals(s.getBranchId()))
                .collect(Collectors.toList());

        List<ShiftAssignment> assignments = shiftAssignmentRepository.findAll();
        List<Attendance> attendances = attendanceRepository.findAll();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        return staffs.stream().map(staff -> {
                    int totalAssignedMins = 0, penaltyMins = 0;
                    int presentCount = 0, absentCount = 0;
                    int totalLateMins = 0, totalEarlyMins = 0;
                    int validShiftsCount = 0;

                    List<Shift> staffShifts = assignments.stream()
                            .filter(a -> a.getStaffId().equals(staff.getId()))
                            .map(a -> allShifts.stream().filter(s -> s.getId().equals(a.getShiftId())).findFirst().orElse(null))
                            .filter(s -> s != null)
                            .collect(Collectors.toList());

                    for (Shift shift : staffShifts) {
                        LocalDateTime shiftStart = getShiftStart(shift);
                        LocalDateTime shiftEnd = getShiftEnd(shift);

                        if (!now.isBefore(shiftStart)) {
                            validShiftsCount++;
                            int shiftDuration = (int) Duration.between(shiftStart, shiftEnd).toMinutes();
                            totalAssignedMins += shiftDuration;

                            Attendance record = attendances.stream()
                                    .filter(a -> a.getShiftId().equals(shift.getId()) && a.getStaffId().equals(staff.getId()))
                                    .findFirst()
                                    .orElse(null);

                            if (record != null) {
                                if (record.getStatus() == AttendanceStatus.ABSENT) {
                                    absentCount++;
                                    penaltyMins += shiftDuration;
                                } else {
                                    presentCount++;
                                    int recLateMins = safeInt(record.getLateMinutes());
                                    int recEarlyMins = safeInt(record.getEarlyLeaveMinutes());
                                    totalLateMins += recLateMins;
                                    totalEarlyMins += recEarlyMins;
                                    penaltyMins += recLateMins + recEarlyMins;
                                }
                            } else {
                                LocalDateTime autoAbsentTime = shiftEnd.plusMinutes(30);
                                if (now.isAfter(autoAbsentTime)) {
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
    @Transactional
    public DashboardOverviewResponse getDashboardOverview(LocalDate date, String branchId) {
        List<Shift> shifts;
        if (branchId != null && !branchId.trim().isEmpty()) {
            shifts = shiftRepository.findAllByDateAndBranchId(date, branchId);
        } else {
            shifts = shiftRepository.findAllByDate(date);
        }

        int totalAssigned = 0, presentCount = 0, absentCount = 0, pendingCount = 0;
        List<TimelineItemResponse> timeline = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        for (Shift shift : shifts) {
            List<ShiftAssignment> assignments = shiftAssignmentRepository.findAllByShiftId(shift.getId());
            List<Attendance> attendances = attendanceRepository.findAllByShiftId(shift.getId());

            int shiftAssignedCount = assignments.size();
            totalAssigned += shiftAssignedCount;
            int shiftPresentCount = 0;

            LocalDateTime autoAbsentTime = getShiftEnd(shift).plusMinutes(30);

            for (ShiftAssignment assignment : assignments) {
                Attendance record = attendances.stream()
                        .filter(a -> a.getStaffId().equals(assignment.getStaffId()))
                        .findFirst()
                        .orElse(null);

                if (record != null) {
                    if (record.getStatus() == AttendanceStatus.PRESENT
                            || record.getStatus() == AttendanceStatus.LATE
                            || record.getStatus() == AttendanceStatus.EARLY_LEAVE) {
                        presentCount++;
                        shiftPresentCount++;
                    } else if (record.getStatus() == AttendanceStatus.ABSENT) {
                        absentCount++;
                    } else {
                        pendingCount++;
                    }
                } else {
                    if (now.isAfter(autoAbsentTime)) {
                        // Tự động lưu ABSENT vào database
                        Attendance autoAbsent = Attendance.builder()
                                .shiftId(shift.getId())
                                .staffId(assignment.getStaffId())
                                .status(AttendanceStatus.ABSENT)
                                .lateMinutes(0)
                                .earlyLeaveMinutes(0)
                                .markedBy("SYSTEM_AUTO")
                                .build();
                        attendanceRepository.save(autoAbsent);

                        // Cập nhật assignment thành CANCELED
                        assignment.setStatus(ScheduleStatus.CANCELED);
                        shiftAssignmentRepository.save(assignment);

                        absentCount++;
                    } else {
                        pendingCount++;
                    }
                }
            }

            boolean isFull = (shiftPresentCount == shiftAssignedCount) && (shiftAssignedCount > 0);
            String timeStr = shift.getStartTime().toString().substring(0, 5) + " - " + shift.getEndTime().toString().substring(0, 5);
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
    @Transactional
    public List<StaffAttendanceDetailsResponse> getStaffAttendanceHistory(String staffId, Integer month, Integer year, LocalDate exactDate, String branchId) {
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

        if (branchId != null && !branchId.trim().isEmpty()) {
            shifts = shifts.stream()
                    .filter(s -> branchId.equals(s.getBranchId()))
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
                        lateMins = safeInt(record.getLateMinutes());
                        earlyMins = safeInt(record.getEarlyLeaveMinutes());
                    } else {
                        LocalDateTime autoAbsentTime = getShiftEnd(shift).plusMinutes(30);
                        if (now.isAfter(autoAbsentTime)) {
                            // Tự động lưu ABSENT vào database nếu quá thời gian
                            Attendance autoAbsent = Attendance.builder()
                                    .shiftId(shift.getId())
                                    .staffId(staffId)
                                    .status(AttendanceStatus.ABSENT)
                                    .lateMinutes(0)
                                    .earlyLeaveMinutes(0)
                                    .markedBy("SYSTEM_AUTO")
                                    .build();
                            attendanceRepository.save(autoAbsent);
                            attStatus = "ABSENT";

                            // Cập nhật ShiftAssignment thành CANCELED
                            shiftAssignmentRepository.findByShiftIdAndStaffId(shift.getId(), staffId)
                                    .ifPresent(asg -> {
                                        asg.setStatus(ScheduleStatus.CANCELED);
                                        shiftAssignmentRepository.save(asg);
                                    });
                        }
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
                }).sorted((a, b) -> {
                    int dateCompare = b.getDate().compareTo(a.getDate());
                    if (dateCompare != 0) return dateCompare;
                    return b.getStartTime().compareTo(a.getStartTime());
                })
                .collect(Collectors.toList());
    }

    private String calculateShiftStatus(Shift shift, LocalDateTime now) {
        LocalDateTime shiftStart = getShiftStart(shift);
        LocalDateTime shiftEnd = getShiftEnd(shift);
        LocalDateTime allowCheckInTime = shiftStart.minusMinutes(30);
        LocalDateTime closeTime = shiftEnd.plusMinutes(30);

        if (now.isBefore(allowCheckInTime)) return "PREPARING";
        else if (now.isAfter(closeTime)) return "CLOSED";
        else return "OPEN";
    }

    private LocalDateTime getShiftStart(Shift shift) {
        return LocalDateTime.of(shift.getDate(), shift.getStartTime());
    }

    private LocalDateTime getShiftEnd(Shift shift) {
        LocalDateTime start = getShiftStart(shift);
        LocalDateTime end = LocalDateTime.of(shift.getDate(), shift.getEndTime());

        // Hỗ trợ ca qua đêm: ví dụ 22:00 -> 06:00
        if (shift.getEndTime().isBefore(shift.getStartTime()) || shift.getEndTime().equals(shift.getStartTime())) {
            end = end.plusDays(1);
        }

        return end;
    }

    private Integer safeInt(Integer value) {
        return value != null ? value : 0;
    }

    private AttendanceResponse toResponse(Attendance a, String staffName) {
        return AttendanceResponse.builder()
                .id(a.getId())
                .shiftId(a.getShiftId())
                .staffId(a.getStaffId())
                .staffName(staffName)
                .status(a.getStatus())
                .lateMinutes(a.getLateMinutes())
                .earlyLeaveMinutes(a.getEarlyLeaveMinutes())
                .markedBy(a.getMarkedBy())
                .markedAt(a.getMarkedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}