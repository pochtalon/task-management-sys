package mate.intro.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfoDto {
    private String nickname;
    private String email;
    private String firstName;
    private String lastName;
}
