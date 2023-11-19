package mate.intro.service;

import mate.intro.dto.label.CreateLabelRequestDto;
import mate.intro.dto.label.LabelDto;
import mate.intro.dto.label.UpdateLabelRequestDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.mapper.LabelMapper;
import mate.intro.model.Label;
import mate.intro.model.Task;
import mate.intro.repository.LabelRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.service.impl.LabelServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LabelServiceImplTest {
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private LabelMapper labelMapper;
    @InjectMocks
    LabelServiceImpl labelService;
    private static final Long LABEL_ID = 7L;
    private static final String NAME = "Label name";
    private static final String COLOR = "RED";
    private static final Long TASK_ID_FIRST = 2L;
    private static final Long TASK_ID_SECOND = 2L;

    @Test
    @DisplayName("Save label with walid data")
    public void save_LabelRequestDto_ReturnLabelDto() {
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto()
                .setName(NAME)
                .setColor(COLOR)
                .setTaskIds(List.of(TASK_ID_FIRST, TASK_ID_SECOND));
        Label label = new Label();
        Task first = new Task().setId(TASK_ID_FIRST);
        Task second = new Task().setId(TASK_ID_SECOND);
        LabelDto expected = new LabelDto();

        when(labelMapper.toModel(requestDto)).thenReturn(label);
        when(taskRepository.findById(TASK_ID_FIRST)).thenReturn(Optional.of(first));
        when(taskRepository.findById(TASK_ID_SECOND)).thenReturn(Optional.of(second));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(expected);

        LabelDto actual = labelService.save(requestDto);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get all labels")
    public void getAll_ReturnListOfLabelDto() {
        Label first = new Label();
        Label second = new Label();
        LabelDto firstDto = new LabelDto();
        LabelDto secondDto = new LabelDto();

        when(labelRepository.findAll()).thenReturn(List.of(first, second));
        when(labelMapper.toDto(first)).thenReturn(firstDto);
        when(labelMapper.toDto(second)).thenReturn(secondDto);

        List<LabelDto> expected = List.of(firstDto, secondDto);
        List<LabelDto> actual = labelService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get labels, return empty list")
    public void getAll_ReturnEmptyList() {
        when(labelRepository.findAll()).thenReturn(Collections.emptyList());

        List<LabelDto> expected = Collections.emptyList();
        List<LabelDto> actual = labelService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete label")
    public void delete_CallLabelRepoOnlyOnce() {
        labelService.deleteLabel(LABEL_ID);
        verify(labelRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(labelRepository);
    }

    @Test
    @DisplayName("Update label with valid dto")
    public void update_ValidRequestDto_ReturnLabelDto() {
        UpdateLabelRequestDto requestDto = createUpdateRequest();
        Task first = new Task().setId(TASK_ID_FIRST);
        Task second = new Task().setId(TASK_ID_SECOND);
        Label currentLabel = new Label();
        Label updatedLabel = new Label()
                .setName(NAME)
                .setColor(Label.Color.RED);
        LabelDto expected = new LabelDto();

        when(labelRepository.findById(LABEL_ID)).thenReturn(Optional.of(currentLabel));
        when(labelMapper.toModel(requestDto)).thenReturn(updatedLabel);
        when(taskRepository.findById(TASK_ID_FIRST)).thenReturn(Optional.of(first));
        when(taskRepository.findById(TASK_ID_SECOND)).thenReturn(Optional.of(second));
        when(labelRepository.save(updatedLabel)).thenReturn(updatedLabel);
        when(labelMapper.toDto(updatedLabel)).thenReturn(expected);

        LabelDto actual = labelService.updateLabel(LABEL_ID, requestDto);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update label with valid dto")
    public void update_InvalidLabelId_ThrowException() {
        UpdateLabelRequestDto requestDto = createUpdateRequest();

        when(labelRepository.findById(LABEL_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> labelService.updateLabel(LABEL_ID, requestDto));
        String expected = "Can't find label with id " + LABEL_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    private UpdateLabelRequestDto createUpdateRequest() {
        return new UpdateLabelRequestDto()
                .setName(NAME)
                .setColor(COLOR)
                .setTaskIds(List.of(TASK_ID_FIRST, TASK_ID_SECOND));
    }
}
