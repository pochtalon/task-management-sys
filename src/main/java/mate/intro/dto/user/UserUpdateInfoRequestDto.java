package mate.intro.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserUpdateInfoRequestDto {
    @Size(min = 8, max = 50)
    private String email;
    @Size(min = 8, max = 100)
    private String password;
    @Size(min = 8, max = 100)
    private String passwordRepeat;
    @Size(max = 50)
    private String nickname;
    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String lastName;
}
