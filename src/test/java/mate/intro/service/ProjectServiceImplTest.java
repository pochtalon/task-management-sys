package mate.intro.service;

import mate.intro.dto.project.CreateProjectRequestDto;
import mate.intro.dto.project.ProjectDto;
import mate.intro.dto.project.UpdateProjectRequestDto;
import mate.intro.dto.task.TaskWithoutProjectDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.mapper.ProjectMapper;
import mate.intro.mapper.TaskMapper;
import mate.intro.model.Project;
import mate.intro.model.Task;
import mate.intro.repository.ProjectRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.service.impl.ProjectServiceImpl;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private TaskMapper taskMapper;
    @InjectMocks
    ProjectServiceImpl projectService;
    private static final Long USER_ID = 15L;
    private static final Long PROJECT_ID = 8L;
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String START = "2024-05-11";
    private static final String END = "2024-10-11";
    private static final String STATUS = "IN_PROGRESS";

    @Test
    @DisplayName("Creating new project")
    public void save_ValidRequestDto_ReturnProjectDto() {
        CreateProjectRequestDto requestDto = new CreateProjectRequestDto();
        Project project = new Project();
        ProjectDto expected = new ProjectDto();

        when(projectMapper.toModel(requestDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(expected);

        ProjectDto actual = projectService.save(requestDto);
        assertEquals(Project.Status.INITIATED, project.getStatus());
        assertEquals(expected, actual);

    }

    @Test
    @DisplayName("Get projects for user")
    public void getProjectsByUserId_UserId_ReturnListOfProjectDto() {
        Project first = new Project();
        Project second = new Project();
        ProjectDto firstDto = new ProjectDto();
        ProjectDto secondDto = new ProjectDto();

        when(projectRepository.findByUserId(anyLong())).thenReturn(List.of(first, second));
        when(projectMapper.toDto(first)).thenReturn(firstDto);
        when(projectMapper.toDto(second)).thenReturn(secondDto);

        List<ProjectDto> expected = List.of(firstDto, secondDto);
        List<ProjectDto> actual = projectService.getProjectsByUserId(USER_ID);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get projects for invalid user")
    public void getProjectsByUserId_InvalidUserId_ReturnEmptyList() {
        when(projectRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());

        List<ProjectDto> expected = Collections.emptyList();
        List<ProjectDto> actual = projectService.getProjectsByUserId(USER_ID);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get projects by valid id")
    public void getById_ValidProjectId_ReturnProjectDto() {
        Project project = new Project();
        ProjectDto expected = new ProjectDto();

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(expected);

        ProjectDto actual = projectService.getById(PROJECT_ID);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get projects by invalid id, throw exception")
    public void getById_InvalidProjectId_ThrowException() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.getById(PROJECT_ID));
        String expected = "Can't find project with id " + PROJECT_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update project with valid data")
    public void updateProject_ProjectIdAndUpdateDto_ReturnProjectDto() {
        UpdateProjectRequestDto updateDto = getUpdateDto();
        Project project = new Project();
        Project fromUpdateDto = getProjectFromDto();
        ProjectDto expected = new ProjectDto();

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(projectMapper.toModel(updateDto)).thenReturn(fromUpdateDto);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(expected);

        ProjectDto actual = projectService.updateProject(PROJECT_ID, updateDto);
        assertEquals(expected, actual);
        assertNotNull(project.getName());
        assertNotNull(project.getDescription());
        assertNotNull(project.getStartDate());
        assertNotNull(project.getEndDate());
        assertNotNull(project.getStatus());
    }

    @Test
    @DisplayName("Update project with valid data")
    public void updateProject_InvalidProjectIdAndUpdateDto_ThrowException() {
        UpdateProjectRequestDto requestDto = new UpdateProjectRequestDto();
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.updateProject(PROJECT_ID, requestDto));
        String expected = "Can't find project with id " + PROJECT_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete project")
    public void delete_CallProjectRepoOnlyOnce() {
        projectService.deleteProject(PROJECT_ID);
        verify(projectRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    @DisplayName("Get task for valid project")
    public void getTaskForProject_ValidProjectId_ReturnListOfTaskWithoutProjectDto() {
        Task first = new Task();
        Task second = new Task();
        TaskWithoutProjectDto firstDto = new TaskWithoutProjectDto();
        TaskWithoutProjectDto secondDto = new TaskWithoutProjectDto();

        when(taskRepository.findAllByProjectId(anyLong())).thenReturn(List.of(first, second));
        when(taskMapper.toDtoWithoutProject(first)).thenReturn(firstDto);
        when(taskMapper.toDtoWithoutProject(second)).thenReturn(secondDto);

        List<TaskWithoutProjectDto> expected = List.of(firstDto, secondDto);
        List<TaskWithoutProjectDto> actual = projectService.getTaskForProject(PROJECT_ID);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get task for invalid project")
    public void getTaskForProject_InvalidProjectId_ReturnEmptyList() {
        when(taskRepository.findAllByProjectId(anyLong())).thenReturn(Collections.emptyList());

        List<TaskWithoutProjectDto> expected = Collections.emptyList();
        List<TaskWithoutProjectDto> actual = projectService.getTaskForProject(PROJECT_ID);
        assertEquals(expected, actual);
    }

    private UpdateProjectRequestDto getUpdateDto() {
        return new UpdateProjectRequestDto()
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setStartDate(START)
                .setEndDate(END)
                .setStatus(STATUS);
    }

    private Project getProjectFromDto() {
        return new Project()
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setStartDate(LocalDate.parse(START))
                .setEndDate(LocalDate.parse(END))
                .setStatus(Project.Status.IN_PROGRESS);
    }
}
