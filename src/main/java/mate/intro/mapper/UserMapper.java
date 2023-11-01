package mate.intro.mapper;

import mate.intro.config.MapperConfig;
import mate.intro.dto.role.UpdateRolesResponseDto;
import mate.intro.dto.user.UserInfoDto;
import mate.intro.dto.user.auth.UserRegistrationRequestDto;
import mate.intro.dto.user.auth.UserResponseDto;
import mate.intro.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = RoleMapper.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto request);

    @Mapping(source = "roles", target = "roleNames")
    UpdateRolesResponseDto toUpdatedRolesDto(User user);

    UserInfoDto toInfoDto(User user);
}
