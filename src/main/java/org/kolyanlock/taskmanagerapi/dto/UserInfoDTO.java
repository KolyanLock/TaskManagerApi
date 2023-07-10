package org.kolyanlock.taskmanagerapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class UserInfoDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    List<String> authorities;
}
