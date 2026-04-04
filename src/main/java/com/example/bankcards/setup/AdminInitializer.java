package com.example.bankcards.setup;


import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.properties.AdminProperties;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AdminProperties.class)
public class AdminInitializer {

    private final AdminProperties adminProperties;
    private final UserService userService;
    private final RoleService roleService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (userService.existsByEmail(adminProperties.getEmail())) {
            log.info("Admin already exists, skipping initialization");
            return;
        }

        Role adminRole = roleService.getRoleByName(UserRole.ROLE_ADMIN);
        User admin = userService.createUser(adminProperties.getEmail(), adminProperties.getPassword(), adminRole);
        log.info("Admin created: email={}", admin.getEmail());
    }
}
