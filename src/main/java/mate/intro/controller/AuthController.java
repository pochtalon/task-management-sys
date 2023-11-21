package mate.intro.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.user.auth.UserLoginRequestDto;
import mate.intro.dto.user.auth.UserLoginResponseDto;
import mate.intro.dto.user.auth.UserRegistrationRequestDto;
import mate.intro.dto.user.auth.UserResponseDto;
import mate.intro.exception.RegistrationException;
import mate.intro.security.AuthenticationService;
import mate.intro.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "register user")
    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @Operation(summary = "login user")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
