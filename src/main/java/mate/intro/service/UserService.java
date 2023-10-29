package mate.intro.service;

import mate.intro.dto.user.UserRegistrationRequestDto;
import mate.intro.dto.user.UserResponseDto;
import mate.intro.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}
