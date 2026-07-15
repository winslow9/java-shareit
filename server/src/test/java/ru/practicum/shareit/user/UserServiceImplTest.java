package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAll() {
        User user = createUser(1L, "Maxim", "maxim@mail.com");

        when(userRepository.findAll()).thenReturn(List.of(user));

        assertThat(userService.getAll()).containsExactly(user);

        verify(userRepository).findAll();
    }

    @Test
    void getById() {
        User user = createUser(1L, "Maxim", "maxim@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertThat(result).isEqualTo(user);
        verify(userRepository).findById(1L);
    }

    @Test
    void saveUser() {
        User user = createUser(null, "Maxim", "maxim@mail.com");
        User savedUser = createUser(1L, "Maxim", "maxim@mail.com");

        when(userRepository.findAll()).thenReturn(List.of());
        when(userRepository.save(user)).thenReturn(savedUser);

        User result = userService.saveUser(user);

        assertThat(result).isEqualTo(savedUser);
        verify(userRepository).findAll();
        verify(userRepository).save(user);
    }


    @Test
    void saveUserWithRepeatEmail() {
        User existingUser = createUser(1L, "Sasha", "maxim@mail.com");
        User newUser = createUser(null, "Maxim", "maxim@mail.com");

        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        assertThrows(ConflictException.class, () -> userService.saveUser(newUser));

        verify(userRepository).findAll();
    }

    @Test
    void updateUser() {
        User oldUser = createUser(1L, "Maxim", "old@mail.ru");
        User updateUser = createUser(null, "Maxim Updated", "test@mail.ru");
        User updatedUser = createUser(1L, "Maxim Updated", "test@mail.ru");

        when(userRepository.findAll()).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updateUser);

        assertThat(result).isEqualTo(updatedUser);

        verify(userRepository).findAll();
        verify(userRepository).findById(1L);
        verify(userRepository).save(oldUser);
    }

    @Test
    void updateUsersWithSameEmail() {
        User existingUser = createUser(2L, "Anna", "anna@mail.com");
        User updateUser = createUser(null, "Max", "anna@mail.com");

        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        assertThrows(ConflictException.class, () -> userService.updateUser(1L, updateUser));

        verify(userRepository).findAll();
    }


    @Test
    void deleteById() {
        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }

    private User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
