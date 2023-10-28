package mate.intro.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginRequestDto {
    @NotEmpty
    @Size(min = 8, max = 20)
    @Email
    private String email;
    @NotEmpty
    @Size(min = 8, max = 20)
    private String password;
}
