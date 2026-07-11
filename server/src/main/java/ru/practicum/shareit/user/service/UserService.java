package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserService {

    Collection<User>  getAll();

    User getById(Long id);

    User saveUser(User user);

    User updateUser(Long id, User user);

    void deleteById(Long id);
}
