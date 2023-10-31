package mate.intro.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.role.UpdateRolesRequestDto;
import mate.intro.dto.role.UpdateRolesResponseDto;
import mate.intro.dto.user.UserInfoDto;
import mate.intro.dto.user.auth.UserRegistrationRequestDto;
import mate.intro.dto.user.auth.UserResponseDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.exception.RegistrationException;
import mate.intro.mapper.RoleMapper;
import mate.intro.mapper.UserMapper;
import mate.intro.model.Role;
import mate.intro.model.User;
import mate.intro.repository.RoleRepository;
import mate.intro.repository.UserRepository;
import mate.intro.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()
                || !request.getPassword().equals(request.getPasswordRepeat())) {
            throw new RegistrationException("Unable to complete registration");
        }
        User user = userMapper.toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = roleRepository.findRoleByName(Role.RoleName.ROLE_USER).orElseThrow(() ->
                new EntityNotFoundException("Can't find role" + Role.RoleName.ROLE_USER.name())
        );
        user.setRoles(Set.of(role));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UpdateRolesResponseDto updateRoles(Long userId, UpdateRolesRequestDto rolesRequest) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id: " + userId + " was not found."));
        Set<Role> userRoles = getRolesForUser(rolesRequest);
        user.setRoles(userRoles);
        return userMapper.toUpdatedRolesDto(userRepository.save(user));
    }

    @Override
    public UserInfoDto getUserInfo(User user) {
        User currentUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find user with id " + user.getId()));
        return userMapper.toInfoDto(currentUser);

    }

    private Set<Role> getRolesForUser(UpdateRolesRequestDto rolesRequest) {
        List<Role> roles = roleRepository.findAll();
        Set<Role> userRoles = new HashSet<>();
        for (Role role : roles) {
            if (rolesRequest.getRoles().contains(role.getName().toString())) {
                userRoles.add(role);
            }
        }
        return userRoles;
    }
}
