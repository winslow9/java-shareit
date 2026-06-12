package ru.yandex.practicum.booking;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import ru.yandex.practicum.booking.dto.BookingDto;
import ru.yandex.practicum.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    // Создать бронирование
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User ID должен быть положительным")
            Long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    // Подтвердить или реджекнуть букинг
    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User ID должен быть положительным")
            Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User ID должен быть положительным")
            Long userId,
            @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User ID должен быть положительным")
            Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getUserBookings(userId, state);
    }

    //Получить все букниги владельца
    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id")
            @Positive(message = "User ID должен быть положительным")
            Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getOwnerBookings(userId, state);
    }
}