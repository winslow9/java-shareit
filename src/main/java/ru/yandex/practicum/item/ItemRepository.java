package ru.yandex.practicum.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> findAll();

    List<Item> findByOwnerId(Long ownerId);

    Optional<Item> findById(Long id);

    Item save(Item item);

    Item update(Item item);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByOwnerIdAndId(Long ownerId, Long itemId);

    List<Item> search(String text);
}