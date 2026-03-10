package com.group4.shift_service.service;

import com.group4.shift_service.dto.request.StaffCreateRequest;
import com.group4.shift_service.dto.request.StaffStatusRequest;
import com.group4.shift_service.dto.response.StaffResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public interface StaffService {
    StaffResponse createStaff(StaffCreateRequest request);
    StaffResponse updateStaff(String id, StaffCreateRequest request);
    void deleteStaff(String id);
    StaffResponse getStaffById(String id);
    Page<StaffResponse> getAllStaffs(int page, int size);
    StaffResponse updateStatus(String id, StaffStatusRequest request);
}