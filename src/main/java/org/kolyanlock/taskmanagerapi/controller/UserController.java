package org.kolyanlock.taskmanagerapi.controller;


import lombok.RequiredArgsConstructor;
import org.kolyanlock.taskmanagerapi.dto.UserDTO;
import org.kolyanlock.taskmanagerapi.dto.UserDeletedDTO;
import org.kolyanlock.taskmanagerapi.facade.UserFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;


@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserFacade userFacade;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(defaultValue = "username") String sortField,
            @PageableDefault(sort = "username", size = 5) Pageable pageable
    ) {
        Sort sort = Sort.by(sortField).and(pageable.getSort());
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<UserDTO> users = userFacade.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        UserDTO user = userFacade.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO)
            throws RoleNotFoundException {
        UserDTO updatedUser = userFacade.updateUser(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDeletedDTO> deleteUser(@PathVariable Long userId) {
        UserDeletedDTO deletedUser = userFacade.deleteUser(userId);
        return ResponseEntity.ok(deletedUser);
    }
}
