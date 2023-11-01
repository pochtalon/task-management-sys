package mate.intro.dto.task;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateTaskRequestDto {
    private String name;
    private String description;
    private String priority;
    private String dueDate;
    private Long projectId;
    private Long assigneeId;
}
