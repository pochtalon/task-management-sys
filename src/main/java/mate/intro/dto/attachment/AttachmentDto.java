package mate.intro.dto.attachment;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AttachmentDto {
    private Long id;
    private Long taskId;
    private String dropboxFile;
    private String fileName;
    private LocalDateTime uploadDate;
}
