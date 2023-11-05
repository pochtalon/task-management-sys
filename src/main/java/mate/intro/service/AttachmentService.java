package mate.intro.service;

import java.util.List;
import mate.intro.dto.attachment.AttachmentDto;
import mate.intro.dto.attachment.CreateAttachmentRequestDto;

public interface AttachmentService {
    AttachmentDto save(CreateAttachmentRequestDto requestDto);

    List<AttachmentDto> getByTaskId(Long taskId);
}
