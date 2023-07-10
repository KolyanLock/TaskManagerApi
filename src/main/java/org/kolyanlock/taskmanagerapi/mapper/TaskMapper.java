package org.kolyanlock.taskmanagerapi.mapper;


import org.kolyanlock.taskmanagerapi.dto.TaskDTO;
import org.kolyanlock.taskmanagerapi.dto.TaskDetailDTO;
import org.kolyanlock.taskmanagerapi.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TaskMapper {
    @Mapping(source = "description.text", target = "description")
    TaskDetailDTO taskToTaskDetailDTO(Task task);

    List<TaskDTO> toTaskDTOList(List<Task> tasks);

    default Page<TaskDTO> toTaskDTOPage(Page<Task> taskPage) {
        List<TaskDTO> taskDTOList = toTaskDTOList(taskPage.getContent());
        return new PageImpl<>(taskDTOList, taskPage.getPageable(), taskPage.getTotalElements());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description.text", source = "description")
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toTaskEntity(TaskDetailDTO taskDetailDTO);
}
