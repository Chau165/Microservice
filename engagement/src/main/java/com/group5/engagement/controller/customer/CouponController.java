package com.group5.engagement.controller.customer;

import com.group5.engagement.dto.request.ApplyCouponRequest;
import com.group5.engagement.dto.request.CouponRequest;
import com.group5.engagement.dto.response.ApiResponse;
import com.group5.engagement.dto.response.ApplyCouponResponse;
import com.group5.engagement.dto.response.CouponResponse;
import com.group5.engagement.entity.Coupon;
import com.group5.engagement.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/engagement/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @PostMapping("/apply")
    public ApiResponse<ApplyCouponResponse> apply(@RequestBody ApplyCouponRequest req){
        ApplyCouponResponse result = couponService.applyCoupon(req);
        return ApiResponse.success(
                result,
                "Áp dụng phiếu giảm giá thành công"
        );
    }

    @GetMapping
    public List<Coupon> getAll()
    {
        return couponService.getAll();
    }
    @PostMapping
    public CouponResponse createCoupon(@RequestBody CouponRequest request) {
        return couponService.createCoupon(request);
    }

    @PutMapping("/{id}")
    public CouponResponse updateCoupon(
            @PathVariable Long id,
            @RequestBody Coupon coupon) {

        return couponService.updateCoupon(id, coupon);
    }

    @DeleteMapping("/{id}")
    public void deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
    }
}
