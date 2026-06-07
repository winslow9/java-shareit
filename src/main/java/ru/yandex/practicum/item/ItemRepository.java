package ru.yandex.practicum.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Поиск всех вещей владельца
    List<Item> findByOwnerIdOrderByIdAsc(Long ownerId);

    // Поиск доступных вещей по названию или описанию
    List<Item> findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameText, String descriptionText);

    // Поиск всех доступных вещей владельца
    List<Item> findByOwnerIdAndAvailableTrue(Long ownerId);

    // Поиск вещей по части названия (без учёта регистра)
    List<Item> findByNameContainingIgnoreCase(String name);
}