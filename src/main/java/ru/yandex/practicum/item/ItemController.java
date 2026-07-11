package ru.yandex.practicum.item;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import ru.yandex.practicum.comment.dto.CommentCreateDto;
import ru.yandex.practicum.comment.dto.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> getUserItems(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User Id should be positive")
            Long userId) {
        return itemService.getUserItemsWithBookings(userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        ItemResponseDto response = itemService.getItemByIdWithBookings(userId, itemId);
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User Id should be positive")
            Long ownerId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User Id should be positive")
            Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemService.update(ownerId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false) String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User Id should be positive")
            Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto commentCreateDto) {
        return itemService.addComment(userId, itemId, commentCreateDto);
    }
}