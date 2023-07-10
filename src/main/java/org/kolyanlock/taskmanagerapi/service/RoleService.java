package org.kolyanlock.taskmanagerapi.service;

import org.kolyanlock.taskmanagerapi.entity.Role;
import org.kolyanlock.taskmanagerapi.role.UserRole;

import javax.management.relation.RoleNotFoundException;

public interface RoleService {
    Role getRoleByUserRole(UserRole userRole) throws RoleNotFoundException;
}
