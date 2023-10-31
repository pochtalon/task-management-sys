package mate.intro.dto.project;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateProjectRequestDto {
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String status;
}
