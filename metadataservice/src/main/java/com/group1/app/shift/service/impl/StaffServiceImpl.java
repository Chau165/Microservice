package com.group1.app.shift.service.impl;

import com.group1.app.shift.dto.request.StaffCreateRequest;
import com.group1.app.shift.dto.request.StaffStatusRequest;
import com.group1.app.shift.dto.response.StaffResponse;
import com.group1.app.shift.entity.Staff;
import com.group1.app.shift.enums.StaffStatus;
import com.group1.app.shift.exception.AppException;
import com.group1.app.shift.exception.ErrorCode;
import com.group1.app.shift.repository.StaffRepository;
import com.group1.app.shift.service.StaffService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffServiceImpl implements StaffService {
    StaffRepository staffRepository;
    WebClient webClient;

    @Value("${auth-service.url:http://localhost:8081}")
    String authServiceUrl;

    @Override
    public StaffResponse createStaff(StaffCreateRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (!StringUtils.hasText(request.getUserId())) {
            errors.put("userId", "User id is required");
        }

        if (staffRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "Email already exists");
        }

        if (staffRepository.existsByPhone(request.getPhone())) {
            errors.put("phone", "Phone number already exists");
        }

        if (StringUtils.hasText(request.getUserId()) && staffRepository.existsByUserId(request.getUserId())) {
            errors.put("userId", "User account is already linked to another staff");
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
                .userId(request.getUserId())
                .managerUserId(request.getManagerUserId())
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

        // Update all fields from request
        staff.setName(request.getName());
        staff.setPhone(request.getPhone());
        staff.setGender(request.getGender());
        staff.setDateOfBirth(request.getDateOfBirth());
        staff.setBranchId(request.getBranchId());
        staff.setManagerUserId(request.getManagerUserId());
        if (StringUtils.hasText(request.getUserId())) {
            staff.setUserId(request.getUserId());
        }

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
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND, id));

        // Chỉ được xóa nếu status là INACTIVE
        if (staff.getStatus() != StaffStatus.INACTIVE) {
            Map<String, String> errors = new HashMap<>();
            errors.put("status", "Staff must be INACTIVE to be deleted");
            throw new AppException(ErrorCode.INVALID_INPUT, errors);
        }

        // Xóa tài khoản user nếu có userId
        if (StringUtils.hasText(staff.getUserId())) {
            try {
                webClient.delete()
                        .uri(authServiceUrl + "/users/{userId}", staff.getUserId())
                        .retrieve()
                        .toBodilessEntity()
                        .block();
            } catch (Exception e) {
                // Log lỗi nhưng vẫn xóa staff (tránh lỗi cascade)
                System.err.println("Failed to delete user account: " + e.getMessage());
            }
        }

        // Xóa staff khỏi database
        staffRepository.deleteById(id);
    }

    @Override
    public StaffResponse getStaffById(String id) {
        return staffRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND, id));
    }

    @Override
    public StaffResponse getStaffByUserId(String userId) {
        return staffRepository.findByUserId(userId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND, userId));
    }

    @Override
    public Page<StaffResponse> getAllStaffs(String managerUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Filter staffs by managerUserId directly
        Page<Staff> staffPage = staffRepository.findByManagerUserId(managerUserId, pageable);
        return staffPage.map(this::mapToResponse);
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
                .userId(s.getUserId())
                .managerUserId(s.getManagerUserId())
                .gender(s.getGender())
                .status(s.getStatus())
                .dateOfBirth(s.getDateOfBirth())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
