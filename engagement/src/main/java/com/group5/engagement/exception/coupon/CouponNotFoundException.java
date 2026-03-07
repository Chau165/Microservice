package com.group5.engagement.exception.coupon;

import com.group5.engagement.exception.BaseException;

public class CouponNotFoundException extends BaseException {
    public CouponNotFoundException() {
        super("COUPON_NOT_FOUND", "Phiếu giảm giá không tồn tại.");
    }
}
