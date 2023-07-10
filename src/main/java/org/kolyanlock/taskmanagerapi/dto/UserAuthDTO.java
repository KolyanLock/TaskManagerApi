package org.kolyanlock.taskmanagerapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {
    @NotEmpty
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$",
            message = "Username must start with a letter and can contain only letters and numbers")
    private String username;

    @NotEmpty
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
//            message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;
}
