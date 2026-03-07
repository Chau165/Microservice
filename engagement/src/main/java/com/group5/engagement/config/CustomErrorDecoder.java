package com.group5.engagement.config;
// Exception tự tạo của bạn
import com.group5.engagement.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        // Lấy status code trả về từ Service kia
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        switch (responseStatus) {
            case NOT_FOUND:
                // Nếu Service kia trả về 404 -> Ném lỗi ResourceNotFoundException
                return new ResourceNotFoundException("Không tìm thấy dữ liệu từ Service liên kết (User/Order/Franchise)");
            
            case BAD_REQUEST:
                return new IllegalArgumentException("Dữ liệu gửi đi không hợp lệ (Bad Request)");
            
            case INTERNAL_SERVER_ERROR:
                 // Có thể log thêm ở đây
                 return new RuntimeException("Lỗi nội bộ từ Service liên kết");

            default:
                // Các lỗi khác thì để Feign xử lý mặc định
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}