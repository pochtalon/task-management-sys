package mate.intro.dto.label;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.intro.dto.task.TaskDto;

@Data
@Accessors(chain = true)
public class LabelDto {
    private Long id;
    private String name;
    private String color;
    private List<TaskDto> tasks;
}
