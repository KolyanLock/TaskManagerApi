package org.kolyanlock.taskmanagerapi.service;

import org.kolyanlock.taskmanagerapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.management.relation.RoleNotFoundException;


public interface UserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    User getCurrentUser();

    User createUser(User user) throws RoleNotFoundException;

    User updateUser(Long userId, User user);

    User getUserById(Long id);

    Page<User> getAllUsers(Pageable pageable);

    User deleteUser(Long id);
}
