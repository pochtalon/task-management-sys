package mate.intro.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCommentRequestDto {
    @NotNull
    private Long taskId;
    @NotBlank
    private String text;
}
