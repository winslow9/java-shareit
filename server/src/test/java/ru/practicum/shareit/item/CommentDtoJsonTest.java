package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void commentDto() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Отличная вещь");
        dto.setAuthorName("Booker");
        dto.setCreated(LocalDateTime.of(2026, 1, 1, 12, 0));

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Отличная вещь");
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("Booker");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-01-01T12:00:00");
    }

    @Test
    void commentDtoFromJson() throws Exception {
        String content =  "{\"id\":1,"
                + "\"text\":\"Отличная вещь\","
                + "\"authorName\":\"Booker\","
                + "\"created\":\"2026-01-01T12:00:00\"}";

        CommentDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Отличная вещь");
        assertThat(dto.getAuthorName()).isEqualTo("Booker");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
    }
}