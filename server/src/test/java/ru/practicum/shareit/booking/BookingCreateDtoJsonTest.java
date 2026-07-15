package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoJsonTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Test
    void testSerialization() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
        dto.setEnd(LocalDateTime.of(2026, 1, 2, 12, 0, 0));

        JsonContent<BookingCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-01-02T12:00:00");

        // Проверка, что все поля присутствуют
        assertThat(result).hasJsonPathValue("$.itemId");
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
    }

    @Test
    void testDeserialization() throws Exception {
        String content = """
                {
                    "itemId": 1,
                    "start": "2026-01-01T12:00:00",
                    "end": "2026-01-02T12:00:00"
                }
                """;

        BookingCreateDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 1, 2, 12, 0, 0));
    }

    @Test
    void testDeserializationWithNanoSeconds() throws Exception {
        String content = """
                {
                    "itemId": 2,
                    "start": "2026-01-01T12:00:00.123456",
                    "end": "2026-01-02T12:00:00.654321"
                }
                """;

        BookingCreateDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(2L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0, 0, 123456000));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 1, 2, 12, 0, 0, 654321000));
    }

    @Test
    void testDeserializationWithTimeZone() throws Exception {
        String content = """
                {
                    "itemId": 3,
                    "start": "2026-01-01T12:00:00+03:00",
                    "end": "2026-01-02T12:00:00+03:00"
                }
                """;

        BookingCreateDto dto = json.parseObject(content);

        // Jackson автоматически конвертирует timezone в LocalDateTime (без timezone)
        assertThat(dto.getItemId()).isEqualTo(3L);
        assertThat(dto.getStart()).isNotNull();
        assertThat(dto.getEnd()).isNotNull();
    }

    @Test
    void testEqualsAndHashCode() {
        BookingCreateDto dto1 = new BookingCreateDto(1L,
                LocalDateTime.of(2026, 1, 1, 12, 0),
                LocalDateTime.of(2026, 1, 2, 12, 0));

        BookingCreateDto dto2 = new BookingCreateDto(1L,
                LocalDateTime.of(2026, 1, 1, 12, 0),
                LocalDateTime.of(2026, 1, 2, 12, 0));

        BookingCreateDto dto3 = new BookingCreateDto(2L,
                LocalDateTime.of(2026, 1, 1, 12, 0),
                LocalDateTime.of(2026, 1, 2, 12, 0));

        // Проверка @Data (equals и hashCode)
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
    }

    @Test
    void testToString() {
        BookingCreateDto dto = new BookingCreateDto(1L,
                LocalDateTime.of(2026, 1, 1, 12, 0),
                LocalDateTime.of(2026, 1, 2, 12, 0));

        String toString = dto.toString();

        // Проверка @ToString
        assertThat(toString).contains("itemId=1");
        assertThat(toString).contains("start=2026-01-01T12:00");
        assertThat(toString).contains("end=2026-01-02T12:00");
    }
}