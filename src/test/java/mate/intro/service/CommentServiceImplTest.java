package mate.intro.service;

import mate.intro.dto.comment.CommentDto;
import mate.intro.dto.comment.CreateCommentRequestDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.mapper.CommentMapper;
import mate.intro.model.Comment;
import mate.intro.model.Task;
import mate.intro.model.User;
import mate.intro.repository.CommentRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.service.impl.CommentServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @InjectMocks
    CommentServiceImpl commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CommentMapper commentMapper;
    private static final Long TASK_ID = 15L;
    private static final Long COMMENT_ID_FIRST = 1L;
    private static final String TEXT = "Text for comment";
    private static final String NICKNAME = "Nick";

    @Test
    @DisplayName("Save comment for user with valid data")
    public void save_ValidRequestDtoAndUser_ValidCommentDto() {
        Task task = new Task().setId(TASK_ID);
        User user = new User()
                .setNickname(NICKNAME);
        CreateCommentRequestDto requestDto = createRequestDto();
        Comment comment = createComment();
        CommentDto expected = createCommentDto();

        when(commentMapper.toModel(requestDto)).thenReturn(comment);
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(commentRepository.save(comment)).thenReturn(comment.setId(COMMENT_ID_FIRST));
        when(commentMapper.toDto(comment)).thenReturn(expected);

        CommentDto actual = commentService.save(requestDto, user);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Save comment for user with invalid task id, throw exception")
    public void save_RequestDtoWithInvalidTaskId_ThrowException() {
        User user = new User()
                .setNickname(NICKNAME);
        CreateCommentRequestDto requestDto = createRequestDto();
        Comment comment = createComment();

        when(commentMapper.toModel(requestDto)).thenReturn(comment);
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.save(requestDto, user));
        String expected = "Can't find task with id " + TASK_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get comments from task without comments, return empty list")
    public void getCommentsByTaskId_TaskId_ReturnEmptyList() {
        List<CommentDto> expected = Collections.emptyList();

        when(commentRepository.findByTaskId(TASK_ID)).thenReturn(Collections.emptyList());

        List<CommentDto> actual = commentService.getCommentsByTaskId(TASK_ID);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get comments from task without comments, return list of CommentDto")
    public void getCommentsByTaskId_TaskId_ReturnListOfCommentDto() {
        Comment comment = createComment();
        CommentDto commentDto = createCommentDto();
        List<CommentDto> expected = List.of(commentDto);

        when(commentRepository.findByTaskId(TASK_ID)).thenReturn(List.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        List<CommentDto> actual = commentService.getCommentsByTaskId(TASK_ID);
        assertEquals(expected, actual);
    }

    private CreateCommentRequestDto createRequestDto() {
        return new CreateCommentRequestDto()
                .setTaskId(TASK_ID)
                .setText(TEXT);
    }

    private Comment createComment() {
        return new Comment()
                .setText(TEXT);
    }

    private CommentDto createCommentDto() {
        return new CommentDto()
                .setId(COMMENT_ID_FIRST)
                .setUser(NICKNAME)
                .setText(TEXT)
                .setTaskId(TASK_ID);
    }
}