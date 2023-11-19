package mate.intro.service;

import mate.intro.dto.role.UpdateRolesRequestDto;
import mate.intro.dto.role.UpdateRolesResponseDto;
import mate.intro.dto.user.UserInfoDto;
import mate.intro.dto.user.UserUpdateInfoRequestDto;
import mate.intro.dto.user.auth.UserRegistrationRequestDto;
import mate.intro.dto.user.auth.UserResponseDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.exception.RegistrationException;
import mate.intro.mapper.UserMapper;
import mate.intro.model.Role;
import mate.intro.model.User;
import mate.intro.repository.RoleRepository;
import mate.intro.repository.UserRepository;
import mate.intro.service.impl.UserServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    UserServiceImpl userService;
    private static final Long USER_ID = 1L;
    private static final String EMAIL = "user@mail.com";
    private static final String PASSWORD = "password";
    private static final String SAVED_PASS = "EnCoDeDpAsSwOrD";
    private static final String FIRST_NAME = "Bob";
    private static final String LAST_NAME = "Marley";
    private static final String NICKNAME = "Nick";

    @Test
    @DisplayName("Register new user")
    public void register_RegistrationRequestDto_ReturnUserResponseDto() throws RegistrationException {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD)
                .setPasswordRepeat(PASSWORD)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME);
        User user = createUser();
        Role role = new Role().setId(1L).setName(Role.RoleName.ROLE_USER);
        UserResponseDto expected = new UserResponseDto()
                .setId(USER_ID)
                .setEmail(EMAIL);

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(SAVED_PASS);
        when(roleRepository.findRoleByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user.setId(USER_ID));
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.register(requestDto);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Register new user with wrong role")
    public void register_RequestDtoWithWrongRoleName_ThrowException() throws RegistrationException {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD)
                .setPasswordRepeat(PASSWORD)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME);
        User user = createUser();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(SAVED_PASS);
        when(roleRepository.findRoleByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> userService.register(requestDto));
        String expected = "Can't find role" + Role.RoleName.ROLE_USER.name();
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Register new user, password and passwordRepeat is not equals")
    public void register_DifferentPassAndPassRepeat_ThrowException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD)
                .setPasswordRepeat(SAVED_PASS);

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RegistrationException.class,
                () -> userService.register(requestDto));
        String expected = "Unable to complete registration";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Register new user, when it's already exist")
    public void register_RegisterExistedUser_ThrowException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail(EMAIL);

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(RegistrationException.class,
                () -> userService.register(requestDto));
        String expected = "Unable to complete registration";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update roles for valid user")
    public void updateRoles_UpdateRoleRequestDto_ReturnUpdatesRolesResponseDto() {
        UpdateRolesRequestDto rolesRequest = new UpdateRolesRequestDto()
                .setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));
        User user = createUser();
        List<Role> roles = getRoleList();
        UpdateRolesResponseDto expected = new UpdateRolesResponseDto()
                .setEmail(EMAIL)
                .setRoleNames(Collections.emptySet());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.findAll()).thenReturn(roles);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUpdatedRolesDto(user)).thenReturn(expected);

        UpdateRolesResponseDto actual = userService.updateRoles(USER_ID, rolesRequest);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update roles for invalid user")
    public void updateRoles_InvalidUser_ThrowException() {
        UpdateRolesRequestDto rolesRequest = new UpdateRolesRequestDto();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateRoles(USER_ID, rolesRequest));
        String expected = "User with id: " + USER_ID + " was not found.";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get info about authenticated user")
    public void getUserInfo_AuthenticatedUser_ReturnUserInfoDto() {
        User user = createUser();
        UserInfoDto expected = new UserInfoDto();

        when(userMapper.toInfoDto(user)).thenReturn(expected);

        UserInfoDto actual = userService.getUserInfo(user);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update user info with valid data")
    public void updateUserInfo_ValidUpdateDto_ReturnUserInfoDto() {
        User user = new User().setId(USER_ID);
        UserUpdateInfoRequestDto infoRequest = new UserUpdateInfoRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD)
                .setPasswordRepeat(PASSWORD)
                .setNickname(NICKNAME)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME);
        UserInfoDto expected = new UserInfoDto();

        when(passwordEncoder.encode(PASSWORD)).thenReturn(SAVED_PASS);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toInfoDto(user)).thenReturn(expected);

        UserInfoDto actual = userService.updateUserInfo(user, infoRequest);
        assertEquals(expected, actual);
    }

    private User createUser() {
        return new User()
                .setEmail(EMAIL)
                .setNickname(NICKNAME)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME);
    }

    private List<Role> getRoleList() {
        Role admin = new Role().setId(1L)
                .setName(Role.RoleName.ROLE_ADMIN);
        Role user = new Role().setId(1L)
                .setName(Role.RoleName.ROLE_USER);
        return List.of(admin, user);
    }
}
