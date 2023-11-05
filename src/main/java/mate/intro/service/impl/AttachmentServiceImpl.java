package mate.intro.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.attachment.AttachmentDto;
import mate.intro.dto.attachment.CreateAttachmentRequestDto;
import mate.intro.dto.dropbox.DropBoxResponseDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.mapper.AttachmentMapper;
import mate.intro.model.Attachment;
import mate.intro.repository.AttachmentRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.service.AttachmentService;
import mate.intro.service.DropBoxService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final DropBoxService dropBoxService;
    private final AttachmentMapper attachmentMapper;

    @Override
    public AttachmentDto save(CreateAttachmentRequestDto requestDto) {
        Attachment attachment = new Attachment();
        attachment.setTask(taskRepository.findById(requestDto.getTaskId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find task with id " + requestDto.getTaskId())));
        DropBoxResponseDto uploadResponse = dropBoxService
                .upload(requestDto.getFilePath(), requestDto.getFileName());
        attachment.setDropboxFile(uploadResponse.getId());
        attachment.setFileName(uploadResponse.getName());
        attachment.setUploadDate(uploadResponse.getClientModified());
        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Override
    public List<AttachmentDto> getByTaskId(Long taskId) {
        List<Attachment> attachments = attachmentRepository.findByTaskId(taskId);
        for (Attachment attachment : attachments) {
            dropBoxService.download(attachment);
        }
        return attachments.stream()
                .map(attachmentMapper::toDto)
                .toList();
    }
}
