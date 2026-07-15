package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Get itemRequest with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getItemRequests(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get itemRequests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getItemAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemsByRequestId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable Long requestId) {
        log.info("Get itemRequest with userId={}, requestId={}", userId, requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> postItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Post itemRequest with userId={}, itemRequestDto={}", userId, itemRequestDto);
        return itemRequestClient.saveItemRequest(userId, itemRequestDto);
    }
}
