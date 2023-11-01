package mate.intro.dto.task;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskWithoutProjectDto {
    private Long id;
    private String name;
    private String description;
    private String priority;
    private String status;
    private String dueDate;
    private String userNickname;
}
