package mate.intro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.project.CreateProjectRequestDto;
import mate.intro.dto.project.ProjectDto;
import mate.intro.dto.project.UpdateProjectRequestDto;
import mate.intro.model.User;
import mate.intro.service.ProjectService;
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

import java.util.List;

@Tag(name = "Project management", description = "Endpoints for managing projects")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create new project", description = "Create new project")
    public ProjectDto addProject(@RequestBody @Valid CreateProjectRequestDto requestDto) {
        return projectService.save(requestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Get user's projects", description = "Get list of projects for current user")
    public List<ProjectDto> getProjects(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return projectService.getProjectsByUserId(user.getId());
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get project by id", description = "Get project by id")
    public ProjectDto getProjectById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Get project by id", description = "Get project by id")
    public ProjectDto updateProject(@PathVariable Long id,
                                    @RequestBody UpdateProjectRequestDto updateRequest) {
        return projectService.updateProject(id, updateRequest);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project by id", description = "Delete project by id")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
