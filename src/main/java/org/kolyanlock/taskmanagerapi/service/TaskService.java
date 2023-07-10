package org.kolyanlock.taskmanagerapi.service;

import org.kolyanlock.taskmanagerapi.entity.Task;
import org.kolyanlock.taskmanagerapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TaskService {
    Page<Task> getTasksForCurrentUser(User currentUser, Pageable pageable);

    Task createTaskForCurrentUser(User currentUser, Task task);

    Task getTaskByIdForCurrentUser(User currentUser, Long taskId);

    Task updateTaskByIdForCurrentUser(User currentUser, Long taskId, Task task);

    Task deleteTaskByIdForCurrentUser(User currentUser, Long taskId);

}
