package service.CSFC.CSFC_auth_service.service;

public interface EmailService {
    void sendEmail(String toEmail, String resetLink);
}
