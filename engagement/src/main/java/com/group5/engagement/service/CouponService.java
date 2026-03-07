package com.group5.engagement.service;


import com.group5.engagement.dto.request.ApplyCouponRequest;
import com.group5.engagement.dto.request.CouponRequest;
import com.group5.engagement.dto.request.GenerateCouponRequest;
import com.group5.engagement.dto.response.ApplyCouponResponse;
import com.group5.engagement.dto.response.CouponResponse;
import com.group5.engagement.dto.response.GenerateCouponResponse;
import com.group5.engagement.entity.Coupon;

import java.util.List;

public interface CouponService {

    ApplyCouponResponse applyCoupon(ApplyCouponRequest request);
    /**
     * Generate bulk coupon codes for a promotion
     * High performance bulk insert with duplicate checking
     */
    GenerateCouponResponse generateCoupons(GenerateCouponRequest request);

    //Validate coupon code
    boolean validateCoupon(String code);

    //Get statistics about coupon generatio
    GenerateCouponResponse.GenerationStats getGenerationStats(Long promotionId);

    List<Coupon> getAll();
    void deleteCoupon(Long id);
    CouponResponse updateCoupon(Long id, Coupon coupon);
    CouponResponse createCoupon(CouponRequest request);

}



