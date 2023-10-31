package mate.intro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.role.UpdateRolesRequestDto;
import mate.intro.dto.role.UpdateRolesResponseDto;
import mate.intro.dto.user.UserInfoDto;
import mate.intro.model.User;
import mate.intro.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role", description = "Change set of roles for user")
    public UpdateRolesResponseDto updateRoles(Authentication authentication,
                                              @PathVariable Long id,
                                              @RequestBody UpdateRolesRequestDto rolesRequest) {
        System.out.println(authentication.getPrincipal());
        return userService.updateRoles(id, rolesRequest);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/me")
    @Operation(summary = "Get user info", description = "Get info for current user")
    public UserInfoDto getUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        System.out.println("User: " + user.toString());
        // todo: wrong username, fix it!package
        return userService.getUserInfo(user);
    }
}
