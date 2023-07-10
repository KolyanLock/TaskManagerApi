package org.kolyanlock.taskmanagerapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kolyanlock.taskmanagerapi.entity.Task;
import org.kolyanlock.taskmanagerapi.entity.User;
import org.kolyanlock.taskmanagerapi.repository.TaskRepository;
import org.kolyanlock.taskmanagerapi.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public Page<Task> getTasksForCurrentUser(User currentUser, Pageable pageable) {
        return taskRepository.findAllByAuthor(currentUser, pageable);
    }

    @Override
    public Task createTaskForCurrentUser(User currentUser, Task task) {
        task.setAuthor(currentUser);
        return taskRepository.save(task);
    }

    @Override
    public Task getTaskByIdForCurrentUser(User currentUser, Long taskId) {
        return taskRepository.findByIdAndAuthor(taskId, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Task not found with ID: %d for user %s", taskId, currentUser.getUsername())));
    }

    @Override
    @Transactional
    public Task updateTaskByIdForCurrentUser(User currentUser, Long taskId, Task task) {
        Task existingTask = getTaskByIdForCurrentUser(currentUser, taskId);

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());

        return taskRepository.save(existingTask);
    }

    @Override
    @Transactional
    public Task deleteTaskByIdForCurrentUser(User currentUser, Long taskId) {
        Task task = getTaskByIdForCurrentUser(currentUser, taskId);
        taskRepository.delete(task);
        return task;
    }
}
