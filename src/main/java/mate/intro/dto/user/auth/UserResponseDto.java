package mate.intro.dto.user.auth;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserResponseDto {
    private Long id;
    private String email;
}
