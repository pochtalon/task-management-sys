package mate.intro.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCommentRequestDto {
    @NotBlank
    private String text;
}
