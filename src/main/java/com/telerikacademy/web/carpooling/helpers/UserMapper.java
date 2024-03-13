package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.dtos.RegisterDto;
import com.telerikacademy.web.carpooling.models.Role;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.dtos.UserDto;
import com.telerikacademy.web.carpooling.repositories.contracts.RoleRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.UserRepository;
import com.telerikacademy.web.carpooling.services.contracts.UserBlockService;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserBlockService userBlockService;

    public UserMapper(UserRepository userRepository, RoleRepository roleRepository, UserBlockService userBlockService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userBlockService = userBlockService;
    }

    public User fromDto(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRole(setUserRole("Regular user"));
        return user;
    }

    public User fromDto(RegisterDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(setUserRole("Regular user"));
        return user;
    }


    public UserShow toDto(User newUser) {
        UserShow userShow = new UserShow(newUser.getUsername(), newUser.getFirstName(), newUser.getLastName(), newUser.getEmail());
        return userShow;
    }

    public UserShowAdmin toDtoAdmin(User newUser) {
        UserShowAdmin userShowAdmin = new UserShowAdmin(newUser.getUsername(), newUser.getFirstName(), newUser.getLastName(), newUser.getEmail(), userBlockService.isUserBlocked(newUser), newUser.getRole().getName());
        return userShowAdmin;
    }


    public User fromDtoUpdate(User userUpdated) {
        try {
            User user = userRepository.getByUsername(userUpdated.getUsername());
            user.setFirstName(userUpdated.getFirstName());
            user.setEmail(userUpdated.getEmail());
            user.setLastName(userUpdated.getLastName());
            user.setPassword(userUpdated.getPassword());
            user.setPhoneNumber(userUpdated.getPhoneNumber());
            return user;
        } catch (EntityNotFoundException e) {
            throw new UnsupportedOperationException("Username cannot be changed!");
        }
    }

    public Role setUserRole(String name) {
        return roleRepository.findByName(name);
    }
}