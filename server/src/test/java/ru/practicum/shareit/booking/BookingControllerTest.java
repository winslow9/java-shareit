package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void saveBooking() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(2).withNano(0);

        BookingCreateDto bookingToSave = new BookingCreateDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(start);
        bookingToSave.setEnd(end);

        Booking savedBooking = createBooking(
                1L,
                start,
                end,
                itemId,
                userId,
                BookingStatus.WAITING
        );

        when(bookingService.save(any(BookingCreateDto.class), eq(userId)))
                .thenReturn(savedBooking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingToSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value(start.toString()))
                .andExpect(jsonPath("$.end").value(end.toString()))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.booker.id").value(userId));

        verify(bookingService).save(any(BookingCreateDto.class), eq(userId));
    }

    @Test
    void updateBookingStatus() throws Exception {
        Long ownerId = 1L;
        Long bookerId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;

        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(2).withNano(0);

        Booking approvedBooking = createBooking(
                bookingId,
                start,
                end,
                itemId,
                bookerId,
                BookingStatus.APPROVED
        );

        when(bookingService.updateStatus(eq(bookingId), eq(ownerId), eq(true)))
                .thenReturn(approvedBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.booker.id").value(bookerId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService).updateStatus(eq(bookingId), eq(ownerId), eq(true));
    }

    @Test
    void getBookingById() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;

        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(2).withNano(0);

        Booking booking = createBooking(
                bookingId,
                start,
                end,
                itemId,
                userId,
                BookingStatus.WAITING
        );

        when(bookingService.getById(eq(bookingId), eq(userId)))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.start").value(start.toString()))
                .andExpect(jsonPath("$.end").value(end.toString()))
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService).getById(eq(bookingId), eq(userId));
    }

    @Test
    void getAllBookingsByBooker() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        String state = "ALL";

        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(2).withNano(0);

        Booking booking = createBooking(
                bookingId,
                start,
                end,
                itemId,
                userId,
                BookingStatus.WAITING
        );

        when(bookingService.getAllByBooker(eq(userId), eq(state)))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[0].item.id").value(itemId))
                .andExpect(jsonPath("$[0].booker.id").value(userId))
                .andExpect(jsonPath("$[0].status").value("WAITING"));

        verify(bookingService).getAllByBooker(eq(userId), eq(state));
    }

    @Test
    void getAllBookingsByOwner() throws Exception {
        Long ownerId = 1L;
        Long bookerId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        String state = "ALL";

        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(2).withNano(0);

        Booking booking = createBooking(
                bookingId,
                start,
                end,
                itemId,
                bookerId,
                BookingStatus.WAITING
        );

        when(bookingService.getAllByOwner(eq(ownerId), eq(state)))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[0].item.id").value(itemId))
                .andExpect(jsonPath("$[0].booker.id").value(bookerId))
                .andExpect(jsonPath("$[0].status").value("WAITING"));

        verify(bookingService).getAllByOwner(eq(ownerId), eq(state));
    }

    private Booking createBooking(Long bookingId,
                                  LocalDateTime start,
                                  LocalDateTime end,
                                  Long itemId,
                                  Long bookerId,
                                  BookingStatus status) {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");

        User booker = new User();
        booker.setId(bookerId);
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");

        Item item = new Item();
        item.setId(itemId);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        return booking;
    }
}