package service.CSFC.CSFC_auth_service.model.util;
import java.security.SecureRandom;

public class PasswordUtil {

    private static final String CHARSET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";

    public static String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            password.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }
        return password.toString();
    }
}