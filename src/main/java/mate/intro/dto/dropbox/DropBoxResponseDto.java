package mate.intro.dto.dropbox;

import com.fasterxml.jackson.annotation.JsonSetter;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DropBoxResponseDto {
    private String id;
    private String name;
    @JsonSetter("client_modified")
    private LocalDateTime clientModified;
}
