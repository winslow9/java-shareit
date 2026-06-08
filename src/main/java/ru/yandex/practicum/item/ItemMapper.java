package ru.yandex.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.user.User;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public Item toEntity(ItemDto itemDto, User user) {
        if (itemDto == null) {
            return null;
        }

        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);

        return item;
    }

}