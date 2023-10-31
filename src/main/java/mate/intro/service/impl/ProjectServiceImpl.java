package mate.intro.service.impl;

import lombok.RequiredArgsConstructor;
import mate.intro.dto.project.CreateProjectRequestDto;
import mate.intro.dto.project.ProjectDto;
import mate.intro.dto.project.UpdateProjectRequestDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.mapper.ProjectMapper;
import mate.intro.model.Project;
import mate.intro.repository.ProjectRepository;
import mate.intro.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto save(CreateProjectRequestDto requestDto) {
        Project project = projectMapper.toModel(requestDto);
        project.setStatus(Project.Status.INITIATED);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> getProjectsByUserId(Long id) {
        return projectRepository.findByUserId(id).stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public ProjectDto getById(Long id) {
        return projectMapper.toDto(projectRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find project with id " + id)));
    }

    @Override
    public ProjectDto updateProject(Long id, UpdateProjectRequestDto updateRequest) {
        Project project = projectRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find project with id " + id));
        Project updates = projectMapper.toUpdatesModel(updateRequest);
        correctProject(project, updates);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private void correctProject(Project project, Project updates) {
        if (updates.getName() != null) {
            project.setName(updates.getName());
        }
        if (updates.getDescription() != null) {
            project.setDescription(updates.getName());
        }
        if (updates.getStartDate() != null) {
            project.setStartDate(updates.getStartDate());
        }
        if (updates.getEndDate() != null) {
            project.setEndDate(updates.getEndDate());
        }
        if (updates.getStatus() != null) {
            project.setStatus(updates.getStatus());
        }
    }
}
