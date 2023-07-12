package org.kolyanlock.taskmanagerapi.facade.impl;

import lombok.RequiredArgsConstructor;
import org.kolyanlock.taskmanagerapi.config.JwtTokenUtil;
import org.kolyanlock.taskmanagerapi.dto.UserAuthDTO;
import org.kolyanlock.taskmanagerapi.dto.UserDTO;
import org.kolyanlock.taskmanagerapi.dto.UserDeletedDTO;
import org.kolyanlock.taskmanagerapi.dto.UserInfoDTO;
import org.kolyanlock.taskmanagerapi.entity.User;
import org.kolyanlock.taskmanagerapi.facade.UserFacade;
import org.kolyanlock.taskmanagerapi.mapper.UserMapper;
import org.kolyanlock.taskmanagerapi.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public UserDTO registerUser(UserAuthDTO userAuthDTO) throws RoleNotFoundException {
        User user = userMapper.toUserEntity(userAuthDTO);
        User createdUser = userService.createUser(user);
        return userMapper.toUserDTO(createdUser);
    }

    @Override
    public String authenticateUserAndGetToken(UserAuthDTO userAuthDTO) {
        String username = userAuthDTO.getUsername();
        String password = userAuthDTO.getPassword();
        UserDetails userDetails = userService.loadUserByUsername(username);
        if (!userDetails.isEnabled()) throw new DisabledException(username + " is disabled");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenUtil.generateToken(userDetails);
    }

    public UserInfoDTO getCurrentUserInfo(Authentication authentication) {
        String name = authentication.getName();
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return new UserInfoDTO(name, authorities);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userService.getUserById(id);
        return userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        userDTO.validateRoles();
        User user = userMapper.toUserEntity(userDTO);
        User updatedUser = userService.updateUser(userId, user);
        return userMapper.toUserDTO(updatedUser);
    }


    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        return userMapper.toUserDTOPage(users);
    }

    @Override
    public UserDeletedDTO deleteUser(Long id) {
        User deletedUser = userService.deleteUser(id);
        return userMapper.toUserDeletedDTO(deletedUser);
    }
}