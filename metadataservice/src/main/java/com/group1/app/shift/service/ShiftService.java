package com.group1.app.shift.service;

import com.group1.app.shift.dto.request.ShiftCreateRequest;
import com.group1.app.shift.dto.request.ShiftUpdateRequest;
import com.group1.app.shift.dto.response.ShiftResponse;
import com.group1.app.shift.dto.response.StaffResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ShiftService {
    ShiftResponse createShift(ShiftCreateRequest request, String user);

    Page<ShiftResponse> getAllShifts(int page, int size, String branchId);

    ShiftResponse getShiftById(String id);

    ShiftResponse updateShift(String id, ShiftUpdateRequest request, String user);

    void deleteShift(String id);

    List<ShiftResponse> getShiftsByDate(LocalDate date, String branchId);

    List<StaffResponse> getStaffByShift(String shiftId);

    void assignStaffToShift(String shiftId, String staffId, String assignedBy);
}