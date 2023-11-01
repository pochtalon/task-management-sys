package mate.intro.service;

import java.util.List;
import mate.intro.dto.project.CreateProjectRequestDto;
import mate.intro.dto.project.ProjectDto;
import mate.intro.dto.project.UpdateProjectRequestDto;
import mate.intro.dto.task.TaskWithoutProjectDto;

public interface ProjectService {
    ProjectDto save(CreateProjectRequestDto requestDto);

    List<ProjectDto> getProjectsByUserId(Long id);

    ProjectDto getById(Long id);

    ProjectDto updateProject(Long id, UpdateProjectRequestDto updateRequest);

    void deleteProject(Long id);

    List<TaskWithoutProjectDto> getTaskForProject(Long id);
}
