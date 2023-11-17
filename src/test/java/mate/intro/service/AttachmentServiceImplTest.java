package mate.intro.service;

import mate.intro.dto.attachment.AttachmentDto;
import mate.intro.dto.attachment.CreateAttachmentRequestDto;
import mate.intro.dto.dropbox.DropBoxResponseDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.exception.RegistrationException;
import mate.intro.mapper.AttachmentMapper;
import mate.intro.model.Attachment;
import mate.intro.model.Task;
import mate.intro.repository.AttachmentRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.service.impl.AttachmentServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {
    @InjectMocks
    AttachmentServiceImpl attachmentService;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private DropBoxService dropBoxService;
    @Mock
    private AttachmentMapper attachmentMapper;
    private static final Long TASK_ID = 10L;
    private static final String DROP_BOX_ID = "Dropbox id";
    private static final String PATH = "Dropbox path";
    private static final String NAME = "Dropbox name";

    @Test
    @DisplayName("Create attachment with valid data")
    public void save_ValidRequestDto_ReturnAttachmentDto() {
        LocalDateTime dateTime = LocalDateTime.now();
        CreateAttachmentRequestDto requestDto = createRequestDto();
        Task task = new Task();
        DropBoxResponseDto uploadResponse = new DropBoxResponseDto()
                .setId(DROP_BOX_ID)
                .setName(NAME)
                .setClientModified(dateTime);
        Attachment attachment = new Attachment()
                .setTask(task)
                .setDropboxFile(DROP_BOX_ID)
                .setFileName(NAME)
                .setUploadDate(dateTime);
        AttachmentDto expected = new AttachmentDto();

        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(dropBoxService.upload(PATH, NAME)).thenReturn(uploadResponse);
        when(attachmentRepository.save(attachment)).thenReturn(attachment);
        when(attachmentMapper.toDto(attachment)).thenReturn(expected);

        AttachmentDto actual = attachmentService.save(requestDto);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Create attachment with invalid task id")
    public void save_InvalidTaskId_ThrowException() {
        CreateAttachmentRequestDto requestDto = createRequestDto();

        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> attachmentService.save(requestDto));
        String expected = "Can't find task with id " + TASK_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get all attachments for task by task id")
    public void getByTaskId_ValidTaskId_ReturnListOfAttachmentDto() {
        Attachment attFirst = new Attachment();
        Attachment attSecond = new Attachment();
        List<Attachment> attachmentList = List.of(attFirst, attSecond);
        AttachmentDto attDtoFirst = new AttachmentDto();
        AttachmentDto attDtoSecond = new AttachmentDto();
        List<AttachmentDto> expected = List.of(attDtoFirst, attDtoSecond);

        when(attachmentRepository.findByTaskId(TASK_ID)).thenReturn(attachmentList);
        doNothing().when(dropBoxService).download(any());
        when(attachmentMapper.toDto(attFirst)).thenReturn(attDtoFirst);
        when(attachmentMapper.toDto(attSecond)).thenReturn(attDtoSecond);

        List<AttachmentDto> actual = attachmentService.getByTaskId(TASK_ID);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get attachments for task by task id, return empty list")
    public void getByTaskId_TaskWithoutAttachments_ReturnEmptyList() {
        List<AttachmentDto> expected = Collections.emptyList();

        when(attachmentRepository.findByTaskId(TASK_ID)).thenReturn(Collections.emptyList());

        List<AttachmentDto> actual = attachmentService.getByTaskId(TASK_ID);
        assertEquals(expected, actual);
    }

    private CreateAttachmentRequestDto createRequestDto() {
        return new CreateAttachmentRequestDto()
                .setTaskId(TASK_ID)
                .setFilePath(PATH)
                .setFileName(NAME);
    }
}
