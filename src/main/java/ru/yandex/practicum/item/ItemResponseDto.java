package ru.yandex.practicum.item;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.booking.dto.BookingInfoDto;
import ru.yandex.practicum.comment.dto.CommentDto;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentDto> comments;
}