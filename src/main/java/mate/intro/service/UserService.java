package mate.intro.service;

import mate.intro.dto.role.UpdateRolesRequestDto;
import mate.intro.dto.role.UpdateRolesResponseDto;
import mate.intro.dto.user.UserInfoDto;
import mate.intro.dto.user.auth.UserRegistrationRequestDto;
import mate.intro.dto.user.auth.UserResponseDto;
import mate.intro.exception.RegistrationException;
import mate.intro.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;

    UpdateRolesResponseDto updateRoles(Long userId, UpdateRolesRequestDto rolesRequest);

    UserInfoDto getUserInfo(User user);
}
