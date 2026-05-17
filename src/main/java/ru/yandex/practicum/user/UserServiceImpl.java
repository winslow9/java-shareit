package ru.yandex.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<UserDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id)));
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("User with email " + userDto.getEmail() + " already exists");
        }
        return mapper.toDto(repository.save(mapper.toEntity(userDto)));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existing.getEmail())) {
            if (repository.existsByEmail(userDto.getEmail())) {
                throw new ConflictException("User with email " + userDto.getEmail() + " already exists");
            }
        }

        mapper.updateEntity(existing, userDto);
        return mapper.toDto(repository.update(existing));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }
}