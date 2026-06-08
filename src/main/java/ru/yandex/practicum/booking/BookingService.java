package ru.yandex.practicum.booking;

import jakarta.transaction.Transactional;
import ru.yandex.practicum.booking.dto.BookingDto;
import ru.yandex.practicum.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    @Transactional
    BookingResponseDto createBooking(Long userId, BookingDto bookingDto);

    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getUserBookings(Long userId, BookingState state);

    List<BookingResponseDto> getOwnerBookings(Long userId, BookingState state);
}