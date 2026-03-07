package org.example.warehouseservice.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    // ================= CATEGORY =================
    CATEGORY_HAS_ITEMS("Category has items", HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND("Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_ALREADY("Category name already exists", HttpStatus.BAD_REQUEST),
    // ================= WAREHOUSE =================
    WAREHOUSE_NOT_FOUND("Warehouse not found", HttpStatus.NOT_FOUND),
    // ================= IMAGE / S3 =================
    NO_IMAGE_FILES("No image files provided", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND("Image not found", HttpStatus.NOT_FOUND),
    UNSUPPORTED_FILE_FORMAT("Unsupported file format (only jpg, jpeg, png allowed)", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("File size must not exceed 5MB", HttpStatus.BAD_REQUEST),
    MAX_IMAGES_EXCEEDED("Maximum number of images exceeded (maximum 5 images per item)", HttpStatus.BAD_REQUEST),
    // ================= ITEM =================
    ITEM_NOT_FOUND("Item not found", HttpStatus.NOT_FOUND),
    ITEM_DELETED("Item has already been deleted ", HttpStatus.BAD_REQUEST),
    ITEM_HAS_STOCK( "Cannot delete item with remaining stock in warehouse locations", HttpStatus.BAD_REQUEST),
    // ================= S3 =================
    S3_UPLOAD_FAILED("Failed to upload image to S3", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_DELETE_FAILED("Failed to delete image from S3", HttpStatus.INTERNAL_SERVER_ERROR);
    String message;
    HttpStatus httpStatus;
    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
