package org.kolyanlock.taskmanagerapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.kolyanlock.taskmanagerapi.entity.Role;
import org.kolyanlock.taskmanagerapi.repository.RoleRepository;
import org.kolyanlock.taskmanagerapi.role.UserRole;
import org.kolyanlock.taskmanagerapi.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByUserRole(UserRole userRole) {
        return roleRepository.findByName(userRole)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UserRole value: " + userRole));
    }
}
