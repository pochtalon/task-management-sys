package mate.intro.service;

import mate.intro.dto.project.CreateProjectRequestDto;
import mate.intro.dto.project.ProjectDto;
import mate.intro.dto.project.UpdateProjectRequestDto;

import java.util.List;

public interface ProjectService {
    ProjectDto save(CreateProjectRequestDto requestDto);

    List<ProjectDto> getProjectsByUserId(Long id);

    ProjectDto getById(Long id);

    ProjectDto updateProject(Long id, UpdateProjectRequestDto updateRequest);

    void deleteProject(Long id);
}
