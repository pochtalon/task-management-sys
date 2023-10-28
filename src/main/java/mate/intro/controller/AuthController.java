package mate.intro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.user.UserLoginRequestDto;
import mate.intro.dto.user.UserLoginResponseDto;
import mate.intro.dto.user.UserRegistrationRequestDto;
import mate.intro.dto.user.UserResponseDto;
import mate.intro.exception.RegistrationException;
import mate.intro.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {

    }
}