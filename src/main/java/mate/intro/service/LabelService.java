package mate.intro.service;

import java.util.List;
import mate.intro.dto.label.CreateLabelRequestDto;
import mate.intro.dto.label.LabelDto;
import mate.intro.dto.label.UpdateLabelRequestDto;

public interface LabelService {
    LabelDto save(CreateLabelRequestDto requestDto);

    List<LabelDto> getAll();

    void deleteLabel(Long id);

    LabelDto updateLabel(Long id, UpdateLabelRequestDto updateRequest);
}
