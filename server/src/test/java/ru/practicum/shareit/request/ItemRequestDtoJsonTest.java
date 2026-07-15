package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void itemRequestDto() throws Exception {
        ItemResponseDto item = new ItemResponseDto();
        item.setId(1L);
        item.setName("Дрель");
        item.setOwnerId(1L);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setIdRequestor(1L);
        dto.setCreated(LocalDateTime.of(2026, 1, 1, 13, 0));
        dto.setItems(List.of(item));

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Нужна дрель");
        assertThat(result).extractingJsonPathNumberValue("$.idRequestor").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-01-01T13:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(1);
    }

    @Test
    void itemRequestDtoFromJson() throws Exception {
        String content =  "{\"id\":1,"
                + "\"description\":\"Нужна дрель\","
                + "\"idRequestor\":1,"
                + "\"created\":\"2026-01-01T13:00:00\","
                + "\"items\":[{\"id\":1,\"name\":\"Дрель\",\"ownerId\":1}]}";

        ItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getIdRequestor()).isEqualTo(1L);
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2026, 1, 1, 13, 0));
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().getFirst().getId()).isEqualTo(1L);
        assertThat(dto.getItems().getFirst().getName()).isEqualTo("Дрель");
        assertThat(dto.getItems().getFirst().getOwnerId()).isEqualTo(1L);
    }
}