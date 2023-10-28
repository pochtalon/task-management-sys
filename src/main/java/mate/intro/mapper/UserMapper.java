package mate.intro.mapper;

import mate.intro.config.MapperConfig;
import mate.intro.dto.user.UserRegistrationRequestDto;
import mate.intro.dto.user.UserResponseDto;
import mate.intro.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto request);
}
