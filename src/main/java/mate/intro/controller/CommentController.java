package mate.intro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.comment.CommentDto;
import mate.intro.dto.comment.CommentRequestDto;
import mate.intro.model.User;
import mate.intro.service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comments management", description = "Endpoints for managing comments")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping()
    @Operation(summary = "Create new project", description = "Create new project")
    public CommentDto addComment(Authentication authentication,
                                 @RequestParam Long taskId,
                                 @RequestBody @Valid CommentRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return commentService.save(requestDto, user, taskId);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping()
    @Operation(summary = "Get comments for task",
            description = "Get all comments for task by task id")
    public List<CommentDto> getCommentsForTask(@RequestParam Long taskId) {
        return commentService.getCommentsByTaskId(taskId);
    }
}
