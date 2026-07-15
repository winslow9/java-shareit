package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );

    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getName(),
                        itemDto.getDescription(),
                        itemDto.getAvailable());
    }

    public static ItemWithDatesDto toItemWithDatesDto(Item item,
                                                             LocalDateTime lastBooking,
                                                             LocalDateTime nextBooking,
                                                             List<CommentDto> comments) {
        return new ItemWithDatesDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static ItemResponseDto toItemResponseDto(Item item) {
        return new ItemResponseDto(item.getId(),
                                   item.getName(),
                                   item.getOwner().getId());
    }
}
