package ru.yandex.practicum.booking;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.booking.dto.BookingInfoDto;
import ru.yandex.practicum.booking.dto.BookingResponseDto;

@Component
public class BookingMapper {

    public BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        BookingResponseDto.BookedItemDto itemDto = new BookingResponseDto.BookedItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        dto.setItem(itemDto);

        BookingResponseDto.BookerDto bookerDto = new BookingResponseDto.BookerDto();
        bookerDto.setId(booking.getBooker().getId());
        bookerDto.setName(booking.getBooker().getName());
        dto.setBooker(bookerDto);

        return dto;
    }

    public BookingInfoDto toBookingInfoDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingInfoDto dto = new BookingInfoDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());

        return dto;
    }
}