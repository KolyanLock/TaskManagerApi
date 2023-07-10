package org.kolyanlock.taskmanagerapi.facade;

import org.kolyanlock.taskmanagerapi.dto.UserDeletedDTO;
import org.kolyanlock.taskmanagerapi.dto.UserInfoDTO;
import org.kolyanlock.taskmanagerapi.dto.UserAuthDTO;
import org.kolyanlock.taskmanagerapi.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import javax.management.relation.RoleNotFoundException;

public interface UserFacade {
    UserDTO registerUser(UserAuthDTO userAuthDTO) throws RoleNotFoundException;

    String authenticateUserAndGetToken(UserAuthDTO userAuthDTO);

    UserInfoDTO getCurrentUserInfo(Authentication authentication);

    UserDTO getUserById(Long id);

    UserDTO updateUser(Long userId, UserDTO userDto) throws RoleNotFoundException;

    Page<UserDTO> getAllUsers(Pageable pageable);

    UserDeletedDTO deleteUser(Long id);

}
