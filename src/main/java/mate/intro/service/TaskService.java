package mate.intro.service;

import mate.intro.dto.task.CreateTaskRequestDto;
import mate.intro.dto.task.TaskDto;
import mate.intro.dto.task.UpdateTaskRequestDto;
import mate.intro.model.User;

public interface TaskService {
    TaskDto save(CreateTaskRequestDto requestDto);

    TaskDto getById(Long taskId, User user);

    TaskDto updateTask(Long id, UpdateTaskRequestDto updateRequest);

    void deleteTask(Long id);
}
