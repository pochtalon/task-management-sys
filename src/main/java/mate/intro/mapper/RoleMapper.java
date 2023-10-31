package mate.intro.mapper;

import mate.intro.config.MapperConfig;
import mate.intro.dto.role.RoleNameDto;
import mate.intro.model.Role;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RoleMapper {
    RoleNameDto toDto(Role role);
}
