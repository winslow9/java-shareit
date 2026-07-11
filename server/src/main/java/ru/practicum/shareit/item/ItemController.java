package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Collection<ItemWithDatesDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAll(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithDatesDto findItemById(@PathVariable Long itemId) {

        return itemService.findById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByText(@RequestParam String text) {

        return itemService.findByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.update(itemDto, userId, itemId));
    }

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.save(itemDto, userId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommnets(@PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody CommentDto commentDto) {

        return CommentMapper.toCommentDto(itemService.addComment(itemId, userId, commentDto));
    }

}
