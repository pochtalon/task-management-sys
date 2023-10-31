package mate.intro.mapper;

import mate.intro.config.MapperConfig;
import mate.intro.dto.project.CreateProjectRequestDto;
import mate.intro.dto.project.ProjectDto;
import mate.intro.dto.project.UpdateProjectRequestDto;
import mate.intro.model.Project;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ProjectMapper {
    Project toModel(CreateProjectRequestDto requestDto);

    ProjectDto toDto(Project project);

    Project toUpdatesModel(UpdateProjectRequestDto updateRequest);
}
