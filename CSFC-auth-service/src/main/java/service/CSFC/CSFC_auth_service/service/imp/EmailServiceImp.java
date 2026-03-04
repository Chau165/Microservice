package service.CSFC.CSFC_auth_service.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.service.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Yêu cầu đặt lại mật khẩu");
        message.setText("Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấp vào liên kết sau để đặt lại mật khẩu của bạn: " + resetLink);

        mailSender.send(message);
    }
}
