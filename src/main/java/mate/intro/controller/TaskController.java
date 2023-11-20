package mate.intro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.task.CreateTaskRequestDto;
import mate.intro.dto.task.TaskDto;
import mate.intro.dto.task.UpdateTaskRequestDto;
import mate.intro.model.User;
import mate.intro.service.TaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Task management", description = "Endpoints for managing tasks")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/tasks")
public class TaskController {
    private final TaskService taskService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create new task", description = "Create new task")
    public TaskDto addTask(@RequestBody @Valid CreateTaskRequestDto requestDto) {
        return taskService.save(requestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get task by id", description = "Get task by id")
    public TaskDto getTaskById(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        return taskService.getById(id, user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update task by id", description = "Update task by id")
    public TaskDto updateTask(@PathVariable Long id,
                                 @RequestBody UpdateTaskRequestDto updateRequest) {
        return taskService.updateTask(id, updateRequest);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task by id", description = "Delete task by id")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
