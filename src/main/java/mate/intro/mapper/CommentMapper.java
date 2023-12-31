package mate.intro.mapper;

import mate.intro.config.MapperConfig;
import mate.intro.dto.comment.CommentDto;
import mate.intro.dto.comment.CreateCommentRequestDto;
import mate.intro.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    Comment toModel(CreateCommentRequestDto requestDto);

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "user.nickname", target = "user")
    CommentDto toDto(Comment comment);
}
