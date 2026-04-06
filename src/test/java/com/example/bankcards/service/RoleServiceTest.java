package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService - получения ролей из БД")
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    @DisplayName("должен вернуть роль если она существует")
    void shouldReturnRole_whenRoleExists() {
        // given
        Role role = new Role(1L, UserRole.ROLE_USER);
        when(roleRepository.findByName(UserRole.ROLE_USER)).thenReturn(Optional.of(role));

        // when
        Role result = roleService.getRoleByName(UserRole.ROLE_USER);

        // then
        assertThat(result).isEqualTo(role);
        verify(roleRepository).findByName(UserRole.ROLE_USER);
    }

    @Test
    @DisplayName("должен выбросить NotFoundException если роль не найдена")
    void shouldThrowNotFoundException_whenRoleNotExists() {
        // given
        when(roleRepository.findByName(UserRole.ROLE_ADMIN)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> roleService.getRoleByName(UserRole.ROLE_ADMIN))
                .isInstanceOf(NotFoundException.class);

        verify(roleRepository).findByName(UserRole.ROLE_ADMIN);
    }
}
