package mate.intro.dto.comment;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommentDto {
    private Long id;
    private Long taskId;
    private String user;
    private String text;
    private LocalDateTime timestamp;
}
