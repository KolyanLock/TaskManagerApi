package org.kolyanlock.taskmanagerapi.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.kolyanlock.taskmanagerapi.role.UserRole;
import org.springframework.security.core.GrantedAuthority;


@Entity
@Table(name = "roles")
@Data
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRole name;

    @Override
    public String getAuthority() {
        return name.name();
    }
}
