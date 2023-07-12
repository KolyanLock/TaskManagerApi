package org.kolyanlock.taskmanagerapi.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.kolyanlock.taskmanagerapi.role.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String username;

    private boolean enabled;

    private Set<RoleDTO> authorities;

    public void validateRoles() {
        String[] validRoles = Arrays.stream(UserRole.values())
                .map(Enum::name).toArray(String[]::new);

        for (RoleDTO roleDTO : authorities) {
            if (!Arrays.asList(validRoles).contains(roleDTO.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UserRole value: " + roleDTO.getName());
            }
        }
    }
}
