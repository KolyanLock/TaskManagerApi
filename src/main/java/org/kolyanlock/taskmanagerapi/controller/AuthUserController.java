package org.kolyanlock.taskmanagerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kolyanlock.taskmanagerapi.dto.TokenDTO;
import org.kolyanlock.taskmanagerapi.dto.UserAuthDTO;
import org.kolyanlock.taskmanagerapi.dto.UserDTO;
import org.kolyanlock.taskmanagerapi.dto.UserInfoDTO;
import org.kolyanlock.taskmanagerapi.facade.UserFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthUserController {
    private final UserFacade userFacade;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserAuthDTO userAuthDTO) {
        try {
            String token = userFacade.authenticateUserAndGetToken(userAuthDTO);
            return ResponseEntity.ok(new TokenDTO(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserAuthDTO userAuthDTO) {
        try {
            UserDTO createdUser = userFacade.registerUser(userAuthDTO);
            return ResponseEntity.ok(createdUser);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        } catch (RoleNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/info")
    public UserInfoDTO getCurrentUserInfo(Authentication authentication) {
        return userFacade.getCurrentUserInfo((authentication));
    }
}
