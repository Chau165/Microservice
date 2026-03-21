package com.group1.app.metadata.controller.franchise;

import com.group1.app.common.response.ApiResponse;
import com.group1.app.metadata.dto.franchisestaff.AssignStaffRequest;
import com.group1.app.metadata.dto.franchisestaff.StaffWithFranchisesResponse;
import com.group1.app.metadata.entity.franchisestaff.FranchiseStaff;
import com.group1.app.metadata.service.FranchiseStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/franchise-service/franchise-staff")
@RequiredArgsConstructor
public class FranchiseStaffController {

    private final FranchiseStaffService staffService;

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('FRANCHISE_STAFF_ASSIGN')")
    public FranchiseStaff assignStaff(@RequestBody AssignStaffRequest request) {

        return staffService.assignStaff(
                request.franchiseId(),
                request.staffId()
        );
    }

    @GetMapping("/franchise/{franchiseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('FRANCHISE_STAFF_VIEW')")
    public ApiResponse<List<FranchiseStaff>> getByFranchise(
            @PathVariable UUID franchiseId) {

        List<FranchiseStaff> result = staffService.getAllByFranchiseId(franchiseId);

        return ApiResponse.success(result);
    }

    @GetMapping("/staff/{staffId}/franchise")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('FRANCHISE_STAFF_VIEW')")
    public ApiResponse<StaffWithFranchisesResponse> getFranchiseByStaffId(@PathVariable String staffId) {

        StaffWithFranchisesResponse result =
                staffService.getFranchiseByStaffId(staffId);

        return ApiResponse.success(result);
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    @PreAuthorize("hasAuthority('FRANCHISE_STAFF_REMOVE')")
    public ApiResponse<String> removeStaff(

            @RequestParam UUID franchiseId,

            @RequestParam String staffId
    ) {

        staffService.removeStaff(franchiseId, staffId);

        return ApiResponse.success("Staff removed from franchise successfully.");
    }
}