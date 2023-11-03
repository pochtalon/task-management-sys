package mate.intro.service;

import java.util.List;
import mate.intro.dto.comment.CommentDto;
import mate.intro.dto.comment.CreateCommentRequestDto;
import mate.intro.model.User;

public interface CommentService {
    CommentDto save(CreateCommentRequestDto requestDto, User user);

    List<CommentDto> getCommentsByTaskId(Long taskId);
}
