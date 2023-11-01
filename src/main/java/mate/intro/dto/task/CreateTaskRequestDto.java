package mate.intro.dto.task;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateTaskRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String priority;
    @NotBlank
    private String dueDate;
    @NotBlank
    private Long projectId;
    @NotBlank
    private Long assigneeId;
}
