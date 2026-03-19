package com.group1.app.shift.service;

import com.group1.app.shift.dto.request.StaffCreateRequest;
import com.group1.app.shift.dto.request.StaffStatusRequest;
import com.group1.app.shift.dto.response.StaffResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public interface StaffService {
    StaffResponse createStaff(StaffCreateRequest request);
    StaffResponse updateStaff(String id, StaffCreateRequest request);
    void deleteStaff(String id);
    StaffResponse getStaffById(String id);
    Page<StaffResponse> getAllStaffs(String managerUserId, int page, int size);
    StaffResponse updateStatus(String id, StaffStatusRequest request);
}