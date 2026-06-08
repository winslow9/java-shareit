package ru.yandex.practicum.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.booking.BookingStatus;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookedItemDto item;
    private BookerDto booker;

    @Data
    public static class BookedItemDto {
        private Long id;
        private String name;
    }

    @Data
    public static class BookerDto {
        private Long id;
        private String name;
    }
}