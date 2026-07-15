package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item save(ItemDto itemDto, Long userId);

    Item update(ItemDto itemDto, Long userId, Long itemId);

    ItemWithDatesDto findById(Long id);

    Collection<ItemWithDatesDto> findAll(Long userId);

    Collection<Item> findByText(String description);

    Comment addComment(Long itemId, Long userId, CommentDto commentDto);
}
