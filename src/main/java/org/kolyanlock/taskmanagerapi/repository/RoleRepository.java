package org.kolyanlock.taskmanagerapi.repository;


import org.kolyanlock.taskmanagerapi.entity.Role;
import org.kolyanlock.taskmanagerapi.role.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRole userRole);

    Set<Role> findAllByNameIn(List<UserRole> collect);
}
