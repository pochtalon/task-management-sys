package mate.intro.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.label.CreateLabelRequestDto;
import mate.intro.dto.label.LabelDto;
import mate.intro.dto.label.UpdateLabelRequestDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.mapper.LabelMapper;
import mate.intro.model.Label;
import mate.intro.model.Task;
import mate.intro.repository.LabelRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.service.LabelService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelDto save(CreateLabelRequestDto requestDto) {
        Label label = labelMapper.toModel(requestDto);
        if (requestDto.getTaskIds() != null) {
            List<Task> tasks = getTasks(requestDto.getTaskIds());
            label.setTasks(tasks);
        }
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    public List<LabelDto> getAll() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toDto)
                .toList();
    }

    @Override
    public void deleteLabel(Long id) {
        labelRepository.deleteById(id);
    }

    @Override
    public LabelDto updateLabel(Long id, UpdateLabelRequestDto updateRequest) {
        Label label = labelRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find label with id " + id));
        correctLabel(label, updateRequest);
        return labelMapper.toDto(labelRepository.save(label));
    }

    private void correctLabel(Label label, UpdateLabelRequestDto updateRequest) {
        Label updateLabel = labelMapper.toModel(updateRequest);
        if (updateLabel.getName() != null) {
            label.setName(updateLabel.getName());
        }
        if (updateLabel.getColor() != null) {
            label.setColor(updateLabel.getColor());
        }
        if (updateRequest.getTaskIds() != null) {
            label.setTasks(getTasks(updateRequest.getTaskIds()));
        }
    }

    private List<Task> getTasks(List<Long> taskIds) {
        List<Task> tasks = new ArrayList<>();
        for (Long id : taskIds) {
            Optional<Task> taskOptional = taskRepository.findById(id);
            taskOptional.ifPresent(tasks::add);
        }
        return tasks;
    }
}
