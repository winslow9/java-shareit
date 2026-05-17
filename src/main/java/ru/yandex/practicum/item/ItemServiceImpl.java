package ru.yandex.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Override
    public List<ItemDto> findByOwnerId(Long ownerId) {
        return repository.findByOwnerId(ownerId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id)));
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found with id: " + ownerId);
        }
        return mapper.toDto(repository.save(mapper.toEntity(itemDto, ownerId)));
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found with id: " + ownerId);
        }

        Item existing = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Item not found or user is not the owner");
        }

        mapper.updateEntity(existing, itemDto);
        return mapper.toDto(repository.update(existing));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return repository.search(text).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}