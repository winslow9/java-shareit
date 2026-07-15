package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Collection<ItemWithDatesDto>> getItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size){

        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return ResponseEntity.ok(itemService.findAll(userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithDatesDto> getItemById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long itemId) {

        log.info("Get item with userId={}, itemId={}", userId, itemId);
        return ResponseEntity.ok(itemService.findById(itemId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<Item>> getItemsByText(
            @RequestParam(name = "text", defaultValue = "") String text,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size){

        log.info("Search items with text={}, from={}, size={}", text, from, size);
        return ResponseEntity.ok(itemService.findByText(text));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {

        log.info("Patch item {}, userId={}", itemId, userId);
        return ResponseEntity.ok(itemService.update(itemDto, userId, itemId));
    }

    @PostMapping
    public ResponseEntity<Item> save(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto) {

        log.info("Post item with userId={}", userId);
        return ResponseEntity.ok(itemService.save(itemDto, userId));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Comment> saveComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long itemId,
            @RequestBody  CommentDto commentDto) {

        log.info("Post comment to item={}, userId={}", itemId, userId);
        return ResponseEntity.ok(itemService.addComment(itemId, userId, commentDto));
    }
}