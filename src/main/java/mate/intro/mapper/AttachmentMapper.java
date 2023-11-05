package mate.intro.mapper;

import mate.intro.config.MapperConfig;
import mate.intro.dto.attachment.AttachmentDto;
import mate.intro.dto.attachment.CreateAttachmentRequestDto;
import mate.intro.model.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    Attachment toModel(CreateAttachmentRequestDto requestDto);

    @Mapping(source = "task.id", target = "taskId")
    AttachmentDto toDto(Attachment attachment);
}
