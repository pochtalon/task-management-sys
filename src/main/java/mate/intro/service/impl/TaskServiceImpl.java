package mate.intro.service.impl;

import lombok.RequiredArgsConstructor;
import mate.intro.dto.task.CreateTaskRequestDto;
import mate.intro.dto.task.TaskDto;
import mate.intro.dto.task.UpdateTaskRequestDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.mapper.TaskMapper;
import mate.intro.model.Role;
import mate.intro.model.Task;
import mate.intro.model.User;
import mate.intro.repository.ProjectRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.repository.UserRepository;
import mate.intro.service.TaskService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskDto save(CreateTaskRequestDto requestDto) {
        Task task = taskMapper.toModel(requestDto);
        task.setStatus(Task.Status.NOT_STARTED);
        task.setProject(projectRepository.findById(requestDto.getProjectId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find project with id "
                        + requestDto.getProjectId())));
        task.setAssignee(userRepository.findById(requestDto.getAssigneeId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find project with id "
                        + requestDto.getProjectId())));
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskDto getById(Long taskId, User user) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("Can't find task with id " + taskId));
        checkAccess(task, user);
        return taskMapper.toDto(task);
    }

    @Override
    public TaskDto updateTask(Long id, UpdateTaskRequestDto updateRequest) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find task with id " + id));
        correctTask(task, updateRequest);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private void correctTask(Task task, UpdateTaskRequestDto updateRequest) {
        Task updatedModel = taskMapper.toModel(updateRequest);
        if (updatedModel.getName() != null) {
            task.setName(updatedModel.getName());
        }
        if (updatedModel.getDescription() != null) {
            task.setDescription(updatedModel.getDescription());
        }
        if (updatedModel.getPriority() != null) {
            task.setPriority(updatedModel.getPriority());
        }
        if (updatedModel.getStatus() != null) {
            task.setStatus(updatedModel.getStatus());
        }
        if (updatedModel.getDueDate() != null) {
            task.setDueDate(updatedModel.getDueDate());
        }
        if (updateRequest.getProjectId() != null) {
            task.setProject(projectRepository.findById(updateRequest.getProjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Can't find project with id "
                    + updateRequest.getProjectId())));
        }
        if (updateRequest.getAssigneeId() != null) {
            task.setAssignee(userRepository.findById(updateRequest.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("Can't find user with id "
                            + updateRequest.getAssigneeId())));
        }
    }

    private void checkAccess(Task task, User user) {
        boolean isAdmin = false;
        for (Role role : user.getRoles()) {
            if (role.getName().equals(Role.RoleName.ROLE_ADMIN)) {
                isAdmin = true;
                break;
            }
        }
        if (!task.getAssignee().getId().equals(user.getId()) && !isAdmin) {
            throw new RuntimeException("You don't have access to this task");
        }
    }
}
