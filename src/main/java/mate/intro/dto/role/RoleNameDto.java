package mate.intro.dto.role;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RoleNameDto {
    private String name;
}
