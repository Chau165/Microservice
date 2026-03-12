package service.CSFC.CSFC_auth_service.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import service.CSFC.CSFC_auth_service.model.dto.internal.CreateStaffInternalRequest;

@Component
@RequiredArgsConstructor
public class InternalShiftClient {

    private final RestTemplate restTemplate;

    @Value("${internal.services.metadata-service}")
    private String shiftServiceUrl;

    public void createStaff(CreateStaffInternalRequest request) {
        String url = shiftServiceUrl + "/internal/staffs";
        restTemplate.postForObject(url, request, Void.class);
    }
}
