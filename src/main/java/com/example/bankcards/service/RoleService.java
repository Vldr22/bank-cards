package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByName(UserRole name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> NotFoundException.roleByName(name.name()));
    }

}
