package mate.intro.dto.role;

import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateRolesResponseDto {
    private String email;
    private Set<RoleNameDto> roleNames;
}
