package mate.intro.dto.label;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateLabelRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String color;
    private List<Long> taskIds;
}
