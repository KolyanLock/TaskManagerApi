package org.kolyanlock.taskmanagerapi.mapper;

import org.kolyanlock.taskmanagerapi.dto.UserDTO;
import org.kolyanlock.taskmanagerapi.dto.UserAuthDTO;
import org.kolyanlock.taskmanagerapi.dto.UserDeletedDTO;
import org.kolyanlock.taskmanagerapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring", uses = RoleMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    UserDTO toUserDTO(User user);

    @Named("deleted")
    @Mapping(target = "status", constant = "Deleted permanently!")
    UserDeletedDTO toUserDeletedDTO (User user);

    List<UserDTO> toUserDTOList(List<User> userList);

    default Page<UserDTO> toUserDTOPage(Page<User> userPage) {
        List<UserDTO> userDTOList = toUserDTOList(userPage.getContent());
        return new PageImpl<>(userDTOList, userPage.getPageable(), userPage.getTotalElements());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
//    @Mapping(target = "tasks", ignore = true)
    User toUserEntity(UserDTO userDTO);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "authorities", ignore = true)
//    @Mapping(target = "tasks", ignore = true)
    User toUserEntity(UserAuthDTO userAuthDTO);
}
