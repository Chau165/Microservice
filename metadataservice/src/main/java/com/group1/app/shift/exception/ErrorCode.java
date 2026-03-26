package com.group1.app.shift.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    SHIFT_NOT_FOUND(404, "Shift not found", HttpStatus.NOT_FOUND),
    STAFF_NOT_FOUND(404, "Staff not found: %s", HttpStatus.NOT_FOUND),
    STAFF_NOT_IN_SHIFT(400, "Staff %s is not assigned to this shift", HttpStatus.BAD_REQUEST),
    STAFF_INACTIVE(400, "Staff is inactive and cannot be assigned to a shift", HttpStatus.BAD_REQUEST),

    PHONE_EXISTED(400, "Phone number already exists", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(400, "Email already exists", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(400, "Invalid input data", HttpStatus.BAD_REQUEST),

    ATTENDANCE_NOT_FOUND(404, "Attendance record not found", HttpStatus.NOT_FOUND),
    ATTENDANCE_NOT_OPEN_YET(400, "Attendance is not open yet. You can check in only 30 minutes before shift starts.", HttpStatus.BAD_REQUEST),
    ATTENDANCE_MARKING_CLOSED(400, "Attendance is closed. You can no longer mark this shift.", HttpStatus.BAD_REQUEST),
    CANNOT_CHECKOUT_BEFORE_CHECKIN(400, "Cannot check out before checking in.", HttpStatus.BAD_REQUEST),

    SHIFT_NOT_MODIFIABLE(400, "Only shifts in PREPARING status can be modified", HttpStatus.BAD_REQUEST),
    STAFF_ALREADY_ASSIGNED(409, "Staff is already assigned to this shift", HttpStatus.CONFLICT);

    final int code;
    final String message;
    final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}