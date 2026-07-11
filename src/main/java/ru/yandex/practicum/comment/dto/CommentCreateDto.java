package ru.yandex.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentCreateDto {

    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}