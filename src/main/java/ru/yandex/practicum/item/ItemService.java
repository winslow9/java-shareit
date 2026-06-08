package ru.yandex.practicum.item;

import ru.yandex.practicum.comment.dto.CommentCreateDto;
import ru.yandex.practicum.comment.dto.CommentDto;


import java.util.List;

public interface ItemService {

    List<ItemDto> findByOwnerId(Long ownerId);

    ItemDto findById(Long id);

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);


    List<ItemDto> search(String text);

    List<ItemResponseDto> getUserItemsWithBookings(Long userId);

    ItemResponseDto getItemByIdWithBookings(Long userId, Long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);

}
