package mate.intro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.attachment.AttachmentDto;
import mate.intro.dto.attachment.CreateAttachmentRequestDto;
import mate.intro.service.AttachmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Attachments management", description = "Endpoints for managing attachments")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping()
    @Operation(summary = "Create new attachment", description = "Create new attachment")
    public AttachmentDto createAttachment(
            @RequestBody @Valid CreateAttachmentRequestDto requestDto) {
        return attachmentService.save(requestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping()
    @Operation(summary = "Get attachments", description = "Get all attachments for task")
    public List<AttachmentDto> getAttachments(@RequestParam Long taskId) {
        return attachmentService.getByTaskId(taskId);
    }
}
