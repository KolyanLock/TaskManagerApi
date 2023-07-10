package org.kolyanlock.taskmanagerapi.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;


@Data
public class RoleDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private String name;
}
