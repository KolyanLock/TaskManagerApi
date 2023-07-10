package org.kolyanlock.taskmanagerapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kolyanlock.taskmanagerapi.entity.Role;
import org.kolyanlock.taskmanagerapi.entity.User;
import org.kolyanlock.taskmanagerapi.repository.RoleRepository;
import org.kolyanlock.taskmanagerapi.repository.TaskRepository;
import org.kolyanlock.taskmanagerapi.repository.UserRepository;
import org.kolyanlock.taskmanagerapi.role.UserRole;
import org.kolyanlock.taskmanagerapi.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    public User createUser(User user) {
        validateUsernameUniqueness(user.getUsername(), null);

        Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UserRole value: " +UserRole.ROLE_USER));
        user.getAuthorities().add(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, User user) {
        validateUsernameUniqueness(user.getUsername(), userId);

        User existingUser = getUserById(userId);

        if (existingUser.isAdmin() && existingUser.isEnabled() && !existingUser.getUsername().equals(getCurrentUser().getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "It is forbidden to change another the administrator user!");
        }

        if (existingUser.isAdmin() && (!user.isEnabled() || !user.isAdmin())) {
            if (userRepository.findByAuthoritiesName(UserRole.ROLE_ADMIN).size() == 1)
                throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT,
                        "You cannot disable yourself or remove the admin role, you are the only administrator!");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setEnabled(user.isEnabled());

        Set<Role> updatedRoles = user.getAuthorities();

        Set<Role> existingRoles = roleRepository.findAllByNameIn(updatedRoles.stream()
                .map(Role::getName).collect(Collectors.toList()));

        existingUser.setAuthorities(existingRoles);

        return userRepository.save(existingUser);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public User deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));

        if (user.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "It is forbidden to delete the administrator user!");
        }

        taskRepository.deleteByAuthorId(userId);
        userRepository.deleteById(userId);

        return user;
    }

    private void validateUsernameUniqueness(String username, Long userId) {
        boolean usernameExists = userId != null ?
                userRepository.existsByUsernameAndIdNot(username, userId) :
                userRepository.existsByUsername(username);

        if (usernameExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, username + " already exists");
        }
    }
}
