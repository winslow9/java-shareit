package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                              booking.getStart(),
                              booking.getEnd(),
                              new BookingItemDto(booking.getItem().getId(), booking.getItem().getName()),
                              new BookerDto(booking.getBooker().getId()),
                              booking.getStatus()
        );
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto) {
        return new Booking(bookingCreateDto.getStart(),
                           bookingCreateDto.getEnd());
    }

}
