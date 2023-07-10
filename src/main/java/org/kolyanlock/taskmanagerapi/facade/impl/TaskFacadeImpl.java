package org.kolyanlock.taskmanagerapi.facade.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kolyanlock.taskmanagerapi.dto.TaskDTO;
import org.kolyanlock.taskmanagerapi.dto.TaskDetailDTO;
import org.kolyanlock.taskmanagerapi.entity.Task;
import org.kolyanlock.taskmanagerapi.entity.User;
import org.kolyanlock.taskmanagerapi.facade.TaskFacade;
import org.kolyanlock.taskmanagerapi.mapper.TaskMapper;
import org.kolyanlock.taskmanagerapi.service.TaskService;
import org.kolyanlock.taskmanagerapi.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TaskFacadeImpl implements TaskFacade {
    private final UserService userService;
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public Page<TaskDTO> getTasksForCurrentUser(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        Page<Task> tasks = taskService.getTasksForCurrentUser(currentUser, pageable);
        return taskMapper.toTaskDTOPage(tasks);
    }

    @Override
    @Transactional
    public TaskDetailDTO createTask(TaskDetailDTO taskDetailDTO) {
        Task task = taskMapper.toTaskEntity(taskDetailDTO);
        User currentUser = userService.getCurrentUser();
        Task createdTask = taskService.createTaskForCurrentUser(currentUser, task);
        return taskMapper.taskToTaskDetailDTO(createdTask);
    }

    @Override
    @Transactional
    public TaskDetailDTO getTaskById(Long taskId) {
        User currentUser = userService.getCurrentUser();
        Task task = taskService.getTaskByIdForCurrentUser(currentUser, taskId);
        return taskMapper.taskToTaskDetailDTO(task);
    }

    @Override
    @Transactional
    public TaskDetailDTO updateTask(Long taskId, TaskDetailDTO taskDetailDTO) {
        Task task = taskMapper.toTaskEntity(taskDetailDTO);
        User currentUser = userService.getCurrentUser();
        Task updatedTask = taskService.updateTaskByIdForCurrentUser(currentUser, taskId, task);
        return taskMapper.taskToTaskDetailDTO(updatedTask);
    }

    @Override
    @Transactional
    public TaskDetailDTO deleteTask(Long taskId) {
        User currentUser = userService.getCurrentUser();
        Task task = taskService.deleteTaskByIdForCurrentUser(currentUser, taskId);
        TaskDetailDTO deletedTask = taskMapper.taskToTaskDetailDTO(task);
        deletedTask.setDescription("Deleted permanently!");
        return deletedTask;
    }
}
