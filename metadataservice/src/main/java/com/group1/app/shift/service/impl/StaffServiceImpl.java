package com.group1.app.shift.service.impl;

import com.group1.app.shift.dto.request.StaffCreateRequest;
import com.group1.app.shift.dto.request.StaffStatusRequest;
import com.group1.app.shift.dto.response.StaffResponse;
import com.group1.app.shift.entity.Staff;
import com.group1.app.shift.exception.AppException;
import com.group1.app.shift.exception.ErrorCode;
import com.group1.app.shift.repository.StaffRepository;
import com.group1.app.shift.service.StaffService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffServiceImpl implements StaffService {
    StaffRepository staffRepository;

    @Override
    public StaffResponse createStaff(StaffCreateRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (staffRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "Email already exists");
        }

        if (staffRepository.existsByPhone(request.getPhone())) {
            errors.put("phone", "Phone number already exists");
        }

        if (!errors.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT, errors);
        }

        // 1. Tạo entity Staff
        Staff staff = Staff.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .branchId(request.getBranchId())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .build();

        // 2. Tự động sinh Staff Code ngay từ đầu (VD: NVA-54321)
        staff.setStaffCode(generateStaffCode(staff.getName(), staff.getPhone()));

        // 3. Lưu vào DB (Bây giờ chỉ cần lưu 1 lần duy nhất, cực kỳ tối ưu hiệu năng!)
        return mapToResponse(staffRepository.save(staff));
    }

    @Override
    public StaffResponse updateStaff(String id, StaffCreateRequest request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND, id));
        staffRepository.findByPhone(request.getPhone()).ifPresent(existingStaff -> {
            if (!existingStaff.getId().equals(id)) {
                throw new AppException(ErrorCode.PHONE_EXISTED);
            }
        });
        staff.setName(request.getName());
        staff.setPhone(request.getPhone());
        staff.setBranchId(request.getBranchId());
        staff.setDateOfBirth(request.getDateOfBirth());
        staff.setGender(request.getGender());



        return mapToResponse(staffRepository.save(staff));
    }

    @Override
    public StaffResponse updateStatus(String id, StaffStatusRequest request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND, id));

        staff.setStatus(request.getStatus());

        return mapToResponse(staffRepository.save(staff));
    }


    @Override
    public void deleteStaff(String id) {
        if (!staffRepository.existsById(id))
            throw new AppException(ErrorCode.STAFF_NOT_FOUND, id);
        staffRepository.deleteById(id);
    }

    @Override
    public StaffResponse getStaffById(String id) {
        return staffRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND, id));
    }

    @Override
    public Page<StaffResponse> getAllStaffs(String managerUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Optional<Staff> managerOpt = staffRepository.findByUserId(managerUserId);
        if (managerOpt.isEmpty()) {
            return Page.empty(pageable);
        }

        String branchId = managerOpt.get().getBranchId();
        if (branchId == null || branchId.isBlank()) {
            return Page.empty(pageable);
        }

        return staffRepository.findAllByBranchId(branchId, pageable).map(this::mapToResponse);
    }



    private String generateStaffCode(String name, String phone) {
        String initials = extractInitials(name);
        String numberPart;

        // Lấy 5 số cuối của điện thoại.
        // Trường hợp SĐT nhập bậy (dưới 5 số) -> Cho Random 5 số.
        if (phone != null && phone.length() >= 5) {
            numberPart = phone.substring(phone.length() - 5);
        } else {
            int randomNum = 10000 + new Random().nextInt(90000); // Từ 10000 đến 99999
            numberPart = String.valueOf(randomNum);
        }

        return initials + "-" + numberPart;
    }

    private String extractInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "ST";
        String[] words = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            initials.append(word.charAt(0));
        }
        String result = initials.toString().toUpperCase();
        return result.length() > 4 ? result.substring(0, 4) : result; // Tối đa lấy 4 ký tự
    }

    private StaffResponse mapToResponse(Staff s) {
        return StaffResponse.builder()
                .id(s.getId())
                .staffCode(s.getStaffCode())
                .name(s.getName())
                .email(s.getEmail())
                .phone(s.getPhone())
                .branchId(s.getBranchId())
                .gender(s.getGender())
                .status(s.getStatus())
                .dateOfBirth(s.getDateOfBirth())
                .createdAt(s.getCreatedAt())
                .build();
    }
}