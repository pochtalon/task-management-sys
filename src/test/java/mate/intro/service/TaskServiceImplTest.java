package mate.intro.service;

import mate.intro.dto.task.CreateTaskRequestDto;
import mate.intro.dto.task.TaskDto;
import mate.intro.dto.task.UpdateTaskRequestDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.mapper.TaskMapper;
import mate.intro.model.Project;
import mate.intro.model.Role;
import mate.intro.model.Task;
import mate.intro.model.User;
import mate.intro.repository.ProjectRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.repository.UserRepository;
import mate.intro.service.impl.TaskServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @InjectMocks
    TaskServiceImpl taskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskMapper taskMapper;
    private static final Long TASK_ID = 8L;
    private static final Long ASSIGNEE_ID = 5L;
    private static final Long PROJECT_ID = 3L;
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Desc";
    private static final String PRIORITY = "MEDIUM";
    private static final String STATUS = "IN_PROGRESS";
    private static final String DUE_DATE = "2024-06-14";

    @Test
    @DisplayName("Creating new task")
    public void save_ValidRequestDto_ReturnTaskDto() {
        CreateTaskRequestDto requestDto = getRequestDto();
        Task task = new Task();
        Project project = new Project();
        User user = new User();
        TaskDto expected = new TaskDto();

        when(taskMapper.toModel(requestDto)).thenReturn(task);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(expected);

        TaskDto actual = taskService.save(requestDto);
        assertNotNull(task.getStatus());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Creating new task with invalid project id")
    public void save_InvalidProjectId_ThrowException() {
        CreateTaskRequestDto requestDto = getRequestDto();
        Task task = new Task();

        when(taskMapper.toModel(requestDto)).thenReturn(task);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.save(requestDto));
        String expected = "Can't find project with id " + PROJECT_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Creating new task with invalid user id")
    public void save_InvalidUserId_ThrowException() {
        CreateTaskRequestDto requestDto = getRequestDto();
        Task task = new Task();
        Project project = new Project();

        when(taskMapper.toModel(requestDto)).thenReturn(task);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.save(requestDto));
        String expected = "Can't find user with id " + ASSIGNEE_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get task by id for admin")
    public void getById_ValidIdAndAdmin_ReturnTaskId() {
        User admin = new User()
                .setRoles(Set.of(new Role().setName(Role.RoleName.ROLE_ADMIN)));
        User assignee = new User()
                .setId(ASSIGNEE_ID);
        Task task = new Task().setAssignee(assignee);
        TaskDto expected = new TaskDto();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(expected);

        TaskDto actual = taskService.getById(TASK_ID, admin);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get task by id for valid user")
    public void getById_ValidIdAndValidUser_ReturnTaskId() {
        User user = new User()
                .setId(ASSIGNEE_ID)
                .setRoles(Set.of(new Role().setName(Role.RoleName.ROLE_USER)));
        Task task = new Task().setAssignee(user);
        TaskDto expected = new TaskDto();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(expected);

        TaskDto actual = taskService.getById(TASK_ID, user);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get task by invalid id")
    public void getById_InvalidId_ThrowException() {
        User user = new User();
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.getById(TASK_ID, user));
        String expected = "Can't find task with id " + TASK_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get task by invalid user")
    public void getById_InvalidUser_ThrowException() {
        User randomUser = new User()
                .setRoles(Set.of(new Role().setName(Role.RoleName.ROLE_USER)));
        User assignee = new User()
                .setId(ASSIGNEE_ID);
        Task task = new Task().setAssignee(assignee);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        Exception exception = assertThrows(RuntimeException.class,
                () -> taskService.getById(TASK_ID, randomUser));
        String expected = "You don't have access to this task";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete task")
    public void delete_CallTaskRepoOnlyOnce() {
        taskService.deleteTask(TASK_ID);
        verify(taskRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("Update task with valid data")
    public void updateTask_ValidTaskIdAndUpdateDto_ReturnTaskDto() {
        UpdateTaskRequestDto updateDto = getUpdateDto()
                .setProjectId(PROJECT_ID)
                .setAssigneeId(ASSIGNEE_ID);
        Task task = new Task();
        Task fromUpdateDto = getUpdatedTask();
        Project project = new Project();
        User assignee = new User();
        TaskDto expected = new TaskDto();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskMapper.toModel(updateDto)).thenReturn(fromUpdateDto);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(assignee));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(expected);

        TaskDto actual = taskService.updateTask(TASK_ID, updateDto);
        assertEquals(expected, actual);
        assertNotNull(task.getName());
        assertNotNull(task.getDescription());
        assertNotNull(task.getStatus());
        assertNotNull(task.getDueDate());
        assertNotNull(task.getPriority());
        assertNotNull(task.getProject());
        assertNotNull(task.getAssignee());
    }

    @Test
    @DisplayName("Update task with invalid task id")
    public void updateTask_InvalidTaskId_ThrowException() {
        UpdateTaskRequestDto updateDto = getUpdateDto();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> taskService.updateTask(TASK_ID, updateDto));
        String expected = "Can't find task with id " + TASK_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update task with invalid project id")
    public void updateTask_InvalidProjectId_ThrowException() {
        UpdateTaskRequestDto updateDto = getUpdateDto()
                .setProjectId(PROJECT_ID);
        Task task = new Task();
        Task fromUpdateDto = getUpdatedTask();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskMapper.toModel(updateDto)).thenReturn(fromUpdateDto);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> taskService.updateTask(TASK_ID, updateDto));
        String expected = "Can't find project with id " + PROJECT_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update task with invalid assignee id")
    public void updateTask_InvalidAssigneeId_ThrowException() {
        UpdateTaskRequestDto updateDto = getUpdateDto()
                .setProjectId(PROJECT_ID)
                .setAssigneeId(ASSIGNEE_ID);
        Task task = new Task();
        Task fromUpdateDto = getUpdatedTask();
        Project project = new Project();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskMapper.toModel(updateDto)).thenReturn(fromUpdateDto);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> taskService.updateTask(TASK_ID, updateDto));
        String expected = "Can't find user with id " + ASSIGNEE_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    private Task getUpdatedTask() {
        return new Task()
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setStatus(Task.Status.IN_PROGRESS)
                .setPriority(Task.Priority.MEDIUM)
                .setDueDate(LocalDate.parse(DUE_DATE));
    }

    private UpdateTaskRequestDto getUpdateDto() {
        return new UpdateTaskRequestDto()
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setPriority(PRIORITY)
                .setStatus(STATUS)
                .setDueDate(DUE_DATE);
    }

    private CreateTaskRequestDto getRequestDto() {
        return new CreateTaskRequestDto()
                .setAssigneeId(ASSIGNEE_ID)
                .setProjectId(PROJECT_ID);
    }
}
