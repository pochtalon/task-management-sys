package mate.intro.service;

import java.util.List;
import mate.intro.dto.comment.CommentDto;
import mate.intro.dto.comment.CommentRequestDto;
import mate.intro.model.User;

public interface CommentService {
    CommentDto save(CommentRequestDto requestDto, User user, Long taskId);

    List<CommentDto> getCommentsByTaskId(Long taskId);
}
