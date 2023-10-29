package mate.intro.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserRegistrationRequestDto {
    @NotBlank
    @Size(min = 8, max = 50)
    private String email;
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
    @NotBlank
    @Size(min = 8, max = 100)
    private String passwordRepeat;
    @NotBlank
    @Size(max = 50)
    private String username;
    @NotBlank
    @Size(max = 50)
    private String firstName;
    @NotBlank
    @Size(max = 50)
    private String lastName;
}
