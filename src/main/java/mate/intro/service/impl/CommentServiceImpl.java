package mate.intro.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.comment.CommentDto;
import mate.intro.dto.comment.CommentRequestDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.mapper.CommentMapper;
import mate.intro.model.Comment;
import mate.intro.model.User;
import mate.intro.repository.CommentRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.service.CommentService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto save(CommentRequestDto requestDto, User user, Long taskId) {
        Comment comment = commentMapper.toModel(requestDto);
        comment.setUser(user);
        comment.setTask(taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("Can't find task with id " + taskId)));
        comment.setTimestamp(LocalDateTime.now());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(commentMapper::toDto)
                .toList();
    }
}
