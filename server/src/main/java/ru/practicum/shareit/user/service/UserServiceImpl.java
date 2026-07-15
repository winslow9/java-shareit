package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        return user;
    }

    @Transactional
    @Override
    public User saveUser(User user) {

        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()));

        if (emailExists) {
            throw new ConflictException("Email уже используется");
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        if (user.getEmail() != null) {


            boolean emailExists = userRepository.findAll().stream()
                    .filter(existingUser -> existingUser.getEmail().equals(user.getEmail()))
                    .anyMatch(existingUser -> !existingUser.getId().equals(id));

            if (emailExists) {
                throw new ConflictException("Email уже используется");
            }
        }

        User changedUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        if (user.getName() != null) {
            changedUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            changedUser.setEmail(user.getEmail());
        }
        return userRepository.save(changedUser);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

}
