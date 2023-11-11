package mate.intro.dto.label;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateLabelRequestDto {
    private String name;
    private String color;
    private List<Long> taskIds;
}
