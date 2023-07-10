package org.kolyanlock.taskmanagerapi.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kolyanlock.taskmanagerapi.dto.TaskDTO;
import org.kolyanlock.taskmanagerapi.dto.TaskDetailDTO;
import org.kolyanlock.taskmanagerapi.facade.TaskFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskFacade taskFacade;

    @GetMapping
    public Page<TaskDTO> getTasks(
            @RequestParam(defaultValue = "updatedAt") String sortField,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC, size = 5) Pageable pageable
    ) {
        Sort sort = Sort.by(sortField).and(pageable.getSort());
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return taskFacade.getTasksForCurrentUser(pageable);
    }

    @PostMapping
    public ResponseEntity<TaskDetailDTO> createTask(@Valid @RequestBody TaskDetailDTO taskDetailDTO) {
        TaskDetailDTO createdTask = taskFacade.createTask(taskDetailDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDetailDTO> getTaskById(@PathVariable Long taskId) {
        TaskDetailDTO task = taskFacade.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDetailDTO> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskDetailDTO taskDetailDTO) {
        TaskDetailDTO updatedTask = taskFacade.updateTask(taskId, taskDetailDTO);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskDetailDTO> deleteTask(@PathVariable Long taskId) {
        TaskDetailDTO deletedTask = taskFacade.deleteTask(taskId);
        return ResponseEntity.ok(deletedTask);
    }
}
