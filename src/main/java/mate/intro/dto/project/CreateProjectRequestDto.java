package mate.intro.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateProjectRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private String startDate;
    private String endDate;
}
