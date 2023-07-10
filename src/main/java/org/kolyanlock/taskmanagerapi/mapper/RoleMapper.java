package org.kolyanlock.taskmanagerapi.mapper;


import org.kolyanlock.taskmanagerapi.dto.RoleDTO;
import org.kolyanlock.taskmanagerapi.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RoleMapper {
    RoleDTO toRoleDTO(Role role);

    Set<RoleDTO> toRoleDTOSet(Set<Role> roles);

    Set<Role> toRoleEntitySet(Set<RoleDTO> roleDTOs);

    @Mapping(target = "id", ignore = true)
    Role toRoleEntity(RoleDTO roleDTO);
}
