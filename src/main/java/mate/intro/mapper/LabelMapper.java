package mate.intro.mapper;

import mate.intro.config.MapperConfig;
import mate.intro.dto.label.CreateLabelRequestDto;
import mate.intro.dto.label.LabelDto;
import mate.intro.dto.label.UpdateLabelRequestDto;
import mate.intro.model.Label;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = TaskMapper.class)
public interface LabelMapper {
    Label toModel(CreateLabelRequestDto requestDto);

    Label toModel(UpdateLabelRequestDto updateRequest);

    LabelDto toDto(Label label);
}
