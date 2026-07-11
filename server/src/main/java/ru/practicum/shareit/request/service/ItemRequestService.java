package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto save(ItemRequestDto itemRequestDto, Long userId);

    Collection<ItemRequestDto> getAllByUserId(Long userId);

    Collection<ItemRequestDto> getAllByOtherUsers(Long userId);

    ItemRequestDto findById(Long requestId);
}
