package ru.yandex.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public void updateEntity(User existing, UserDto updates) {
        if (updates.getName() != null) {
            existing.setName(updates.getName());
        }
        if (updates.getEmail() != null) {
            existing.setEmail(updates.getEmail());
        }
    }
}