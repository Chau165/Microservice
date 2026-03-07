package com.group5.engagement.exception.coupon;

import com.group5.engagement.exception.BaseException;

public class InvalidCouponException extends BaseException {
    public InvalidCouponException(String message) {
        super("INVALID_COUPON", message);
    }
}
