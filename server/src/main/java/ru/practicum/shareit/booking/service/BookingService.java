package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import java.util.Collection;

public interface BookingService {

    Booking save(BookingCreateDto bookingCreateDto, Long userId);

    Booking updateStatus(Long bookingId, Long userId, Boolean approved);

    Booking getById(Long bookingId, Long userId);

    Collection<Booking> getAllByBooker(Long userId, String state);

    Collection<Booking> getAllByOwner(Long ownerId, String state);

}
