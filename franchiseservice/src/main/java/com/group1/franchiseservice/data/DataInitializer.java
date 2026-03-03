package com.group1.franchiseservice.data;

import com.group1.franchiseservice.model.entity.Account;
import com.group1.franchiseservice.model.entity.Role;
import com.group1.franchiseservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.count() > 0) return;

        Account admin = Account.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        Account manager = Account.builder()
                .username("manager")
                .password(passwordEncoder.encode("123456"))
                .role(Role.FRANCHISE_MANAGER)
                .enabled(true)
                .build();

        Account logistic = Account.builder()
                .username("logistic")
                .password(passwordEncoder.encode("123456"))
                .role(Role.LOGISTIC_ADMIN)
                .enabled(true)
                .build();

        Account customer = Account.builder()
                .username("customer")
                .password(passwordEncoder.encode("123456"))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        userRepository.saveAll(List.of(admin, manager, logistic, customer));
    }
}
