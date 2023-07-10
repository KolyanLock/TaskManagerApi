package org.kolyanlock.taskmanagerapi.facade;

import org.kolyanlock.taskmanagerapi.dto.TaskDTO;
import org.kolyanlock.taskmanagerapi.dto.TaskDetailDTO;
import org.kolyanlock.taskmanagerapi.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TaskFacade {
    Page<TaskDTO> getTasksForCurrentUser(Pageable pageable);

    TaskDetailDTO createTask(TaskDetailDTO taskDetailDTO);

    TaskDetailDTO getTaskById(Long taskId);

    TaskDetailDTO updateTask(Long taskId, TaskDetailDTO taskDetailDTO);

    TaskDetailDTO deleteTask(Long taskId);
}
