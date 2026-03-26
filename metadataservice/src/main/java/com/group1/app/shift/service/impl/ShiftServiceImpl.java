package com.group1.app.shift.service.impl;

import com.group1.app.shift.dto.request.ShiftCreateRequest;
import com.group1.app.shift.dto.request.ShiftUpdateRequest;
import com.group1.app.shift.dto.response.ShiftResponse;
import com.group1.app.shift.dto.response.StaffResponse;
import com.group1.app.shift.entity.Shift;
import com.group1.app.shift.entity.ShiftAssignment;
import com.group1.app.shift.entity.Staff;
import com.group1.app.shift.enums.StaffStatus;
import com.group1.app.shift.exception.AppException;
import com.group1.app.shift.exception.ErrorCode;
import com.group1.app.shift.repository.ShiftAssignmentRepository;
import com.group1.app.shift.repository.ShiftRepository;
import com.group1.app.shift.repository.StaffRepository;
import com.group1.app.shift.service.ShiftService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShiftServiceImpl implements ShiftService {

    ShiftRepository shiftRepository;
    ShiftAssignmentRepository shiftAssignmentRepository;
    StaffRepository staffRepository;

    @Override
    public ShiftResponse createShift(ShiftCreateRequest request, String user) {
        Shift shift = Shift.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .branchId(request.getBranchId())
                .createBy(user)
                .build();
        return mapToResponse(shiftRepository.save(shift));
    }

    @Override
    public Page<ShiftResponse> getAllShifts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return shiftRepository.findAll(pageable).map(this::mapToResponse);
    }

    public Page<ShiftResponse> getAllShiftsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return shiftRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public ShiftResponse getShiftById(String id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_FOUND));
        return mapToResponse(shift);
    }

    @Override
    public ShiftResponse updateShift(String id, ShiftUpdateRequest request, String user) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_FOUND));

        if (!calculateStatus(shift).equals("PREPARING")) {
            throw new AppException(ErrorCode.SHIFT_NOT_MODIFIABLE);
        }

        shift.setDate(request.getDate());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setBranchId(request.getBranchId());
        shift.setUpdateBy(user);

        return mapToResponse(shiftRepository.save(shift));
    }

    @Override
    public void deleteShift(String id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_FOUND));

        if (!calculateStatus(shift).equals("PREPARING")) {
            throw new AppException(ErrorCode.SHIFT_NOT_MODIFIABLE);
        }
        shiftRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftResponse> getShiftsByDate(LocalDate date) {
        return shiftRepository.findAllByDate(date)
                .stream()
                .map(shift -> {
                    int staffCount = shiftAssignmentRepository
                            .findAllByShiftId(shift.getId()).size();
                    return mapToResponse(shift, staffCount);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffByShift(String shiftId) {
        if (!shiftRepository.existsById(shiftId)) {
            throw new AppException(ErrorCode.SHIFT_NOT_FOUND);
        }

        List<String> staffIds = shiftAssignmentRepository
                .findAllByShiftId(shiftId)
                .stream()
                .map(ShiftAssignment::getStaffId)
                .toList();

        return staffRepository.findAllById(staffIds)
                .stream()
                .map(this::toStaffResponse)
                .toList();
    }

    @Override
    @Transactional
    public void assignStaffToShift(String shiftId, String staffId, String assignedBy) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_FOUND));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND, staffId));

        if (staff.getStatus() == StaffStatus.INACTIVE) {
            throw new AppException(ErrorCode.STAFF_INACTIVE);
        }

        boolean alreadyAssigned = shiftAssignmentRepository.findAllByShiftId(shiftId)
                .stream().anyMatch(a -> a.getStaffId().equals(staffId));

        if (alreadyAssigned) {
            throw new AppException(ErrorCode.STAFF_ALREADY_ASSIGNED);
        }

        ShiftAssignment assignment = ShiftAssignment.builder()
                .shiftId(shiftId)
                .staffId(staffId)
                .assignedBy(assignedBy)
                // save denormalized fields so schedule queries are fast
                .date(shift.getDate())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .branchId(shift.getBranchId())
                .build();

        shiftAssignmentRepository.save(assignment);
    }

    /**
     * =================================================================
     * LOGIC CẬP NHẬT: THÊM THỜI GIAN ÂN HẠN (GRACE PERIOD)
     * =================================================================
     */
    private String calculateStatus(Shift shift) {
        // Lấy giờ hệ thống chuẩn VN
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime shiftStart = LocalDateTime.of(shift.getDate(), shift.getStartTime());
        LocalDateTime shiftEnd = LocalDateTime.of(shift.getDate(), shift.getEndTime());

        // 1. Cho phép Check-in sớm: Mở cửa trước 30 phút
        LocalDateTime allowCheckInTime = shiftStart.minusMinutes(30);

        // 2. Cho phép Check-out trễ: Khóa sổ sau 30 phút kể từ lúc hết ca
        // (Trong thời gian này Admin vẫn thoải mái thao tác)
        LocalDateTime closeTime = shiftEnd.plusMinutes(30);

        if (now.isBefore(allowCheckInTime)) {
            return "PREPARING";
        } else if (now.isAfter(closeTime)) {
            return "CLOSED";
        } else {
            // Bao gồm từ [Bắt đầu - 30p] đến [Kết thúc + 30p]
            return "OPEN";
        }
    }

    private ShiftResponse mapToResponse(Shift shift) {
        return ShiftResponse.builder()
                .id(shift.getId())
                .date(shift.getDate())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .branchId(shift.getBranchId())
                .status(calculateStatus(shift))
                .createBy(shift.getCreateBy())
                .createAt(shift.getCreateAt())
                .build();
    }

    private ShiftResponse mapToResponse(Shift shift, int staffCount) {
        return ShiftResponse.builder()
                .id(shift.getId())
                .date(shift.getDate())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .branchId(shift.getBranchId())
                .status(calculateStatus(shift))
                .createBy(shift.getCreateBy())
                .createAt(shift.getCreateAt())
                .staffCount(staffCount)
                .build();
    }

    private StaffResponse toStaffResponse(Staff s) {
        return StaffResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .email(s.getEmail())
                .branchId(s.getBranchId())
                .status(s.getStatus())
                .build();
    }
}