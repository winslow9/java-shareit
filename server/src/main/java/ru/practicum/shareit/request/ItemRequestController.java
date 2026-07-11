package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public Collection<ItemRequestDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemRequestService.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllByOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemRequestService.getAllByOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable Long requestId) {

        return itemRequestService.findById(requestId);
    }

    @PostMapping
    public ItemRequestDto saveItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody ItemRequestDto itemRequestDto) {

        return itemRequestService.save(itemRequestDto, userId);
    }
}
