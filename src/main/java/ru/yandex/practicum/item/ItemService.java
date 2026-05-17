package ru.yandex.practicum.item;

import java.util.List;

public interface ItemService {
    List<ItemDto> findByOwnerId(Long ownerId);

    ItemDto findById(Long id);

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);

    List<ItemDto> search(String text);
}