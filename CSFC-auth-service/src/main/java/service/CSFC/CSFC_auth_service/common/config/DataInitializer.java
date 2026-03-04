package service.CSFC.CSFC_auth_service.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import service.CSFC.CSFC_auth_service.model.entity.Roles;
import service.CSFC.CSFC_auth_service.model.entity.Users;
import service.CSFC.CSFC_auth_service.repository.RolesRepository;
import service.CSFC.CSFC_auth_service.repository.UsersRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RolesRepository rolesRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== DataInitializer: Bắt đầu khởi tạo dữ liệu mặc định ===");

        // ── Tạo roles nếu chưa có ──────────────────────────────────────────
        Roles adminRole = rolesRepository.findByName("ADMIN").orElseGet(() -> {
            Roles r = new Roles();
            r.setName("ADMIN");
            r.setCreateDate(LocalDateTime.now());
            Roles saved = rolesRepository.save(r);
            log.info("Đã tạo role: ADMIN");
            return saved;
        });

        Roles customerRole = rolesRepository.findByName("CUSTOMER").orElseGet(() -> {
            Roles r = new Roles();
            r.setName("CUSTOMER");
            r.setCreateDate(LocalDateTime.now());
            Roles saved = rolesRepository.save(r);
            log.info("Đã tạo role: CUSTOMER");
            return saved;
        });

        // ── Tạo tài khoản ADMIN ────────────────────────────────────────────
        String adminEmail = "admin@csfc.com";
        if (!usersRepository.existsByEmail(adminEmail)) {
            Users admin = new Users();
            admin.setName("Administrator");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(adminRole);
            admin.setIsActive(true);
            admin.setIsFirstLogin(false);
            admin.setCreateDate(LocalDateTime.now());
            usersRepository.save(admin);
            log.info("Đã tạo tài khoản ADMIN  → email: {}  password: Admin@123", adminEmail);
        } else {
            log.info("Tài khoản ADMIN đã tồn tại, bỏ qua.");
        }

        // ── Tạo tài khoản CUSTOMER ─────────────────────────────────────────
        String customerEmail = "customer@csfc.com";
        if (!usersRepository.existsByEmail(customerEmail)) {
            Users customer = new Users();
            customer.setName("Customer Demo");
            customer.setEmail(customerEmail);
            customer.setPassword(passwordEncoder.encode("Customer@123"));
            customer.setRole(customerRole);
            customer.setIsActive(true);
            customer.setIsFirstLogin(true);
            customer.setCreateDate(LocalDateTime.now());
            usersRepository.save(customer);
            log.info("Đã tạo tài khoản CUSTOMER → email: {}  password: Customer@123", customerEmail);
        } else {
            log.info("Tài khoản CUSTOMER đã tồn tại, bỏ qua.");
        }

        log.info("=== DataInitializer: Hoàn tất ===");
    }
}
