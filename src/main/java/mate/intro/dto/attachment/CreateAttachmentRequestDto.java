package mate.intro.dto.attachment;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateAttachmentRequestDto {
    private Long taskId;
    private String fileName;
    private String filePath;
}
