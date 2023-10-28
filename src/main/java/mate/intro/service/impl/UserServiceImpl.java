package mate.intro.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.user.UserRegistrationRequestDto;
import mate.intro.dto.user.UserResponseDto;
import mate.intro.exception.EntityNotFoundException;
import mate.intro.exception.RegistrationException;
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
}
