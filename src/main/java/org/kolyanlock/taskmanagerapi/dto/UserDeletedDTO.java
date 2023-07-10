package org.kolyanlock.taskmanagerapi.dto;


import lombok.Data;

import java.util.Set;

@Data
public class UserDeletedDTO {
    private Long id;

    private String username;

    private boolean enabled;

    private Set<RoleDTO> authorities;

    private String status;
}
