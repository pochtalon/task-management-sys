package mate.intro.mapper;

import mate.intro.config.MapperConfig;
import mate.intro.dto.task.CreateTaskRequestDto;
import mate.intro.dto.task.TaskDto;
import mate.intro.dto.task.TaskWithoutProjectDto;
import mate.intro.dto.task.UpdateTaskRequestDto;
import mate.intro.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface TaskMapper {
    Task toModel(CreateTaskRequestDto requestDto);

    Task toModel(UpdateTaskRequestDto updateRequest);

    @Mapping(source = "project.name", target = "projectName")
    @Mapping(source = "assignee.nickname", target = "userNickname")
    TaskDto toDto(Task task);

    TaskWithoutProjectDto toDtoWithoutProject(Task task);
}
