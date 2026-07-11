package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void saveBookingWithWaitingStatus() {
        User owner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(1L, true, owner);
        BookingCreateDto dto = createBookingCreateDto(item.getId());
        Booking savedBooking = createBooking(1L, item, booker, BookingStatus.WAITING);

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        Booking result = bookingService.save(dto, 2L);

        assertThat(result).isEqualTo(savedBooking);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void saveWhenUserNotFound() {
        BookingCreateDto dto = createBookingCreateDto(1L);

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.save(dto, 2L));
    }

    @Test
    void saveWhenItemNotFound() {
        User booker = createUser(1L);
        BookingCreateDto dto = createBookingCreateDto(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.save(dto, 1L));
    }

    @Test
    void updateStatusWhenApprovedTrue() {
        User owner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(1L, true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.updateStatus(1L, 1L, true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);

        verify(bookingRepository).findById(1L);
    }

    @Test
    void updateStatusWhenApprovedFalse() {
        User owner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(1L, true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.updateStatus(1L, 1L, false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);

        verify(bookingRepository).findById(1L);
    }

    @Test
    void updateStatusWhenBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(1L, 1L, true));
    }

    @Test
    void updateStatusWhenUserIsNotOwner() {
        User owner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(1L, true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateStatus(1L, 3L, true));
    }

    @Test
    void updateStatusWhenBookingAlreadyProcessed() {
        User owner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(1L, true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateStatus(1L, 1L, true));
    }

    @Test
    void getByIdWhenUserIsBooker() {
        User owner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(1L, true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getById(1L, 2L);

        assertThat(result).isEqualTo(booking);
    }

    @Test
    void getByIdWhenUserIsOwner() {
        User owner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(1L, true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getById(1L, 1L);

        assertThat(result).isEqualTo(booking);
    }

    @Test
    void getByIdWhenBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 1L));
    }


    @Test
    void getAllByBookerWhenUserNotFound() {
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getAllByBooker(2L, "ALL"));
    }

    @Test
    void getAllByBooker() {
        User owner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(1L, true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(2L)).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(2L), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(2L), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(eq(2L), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(2L, BookingStatus.WAITING))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(2L, BookingStatus.REJECTED))
                .thenReturn(List.of(booking));

        assertThat(bookingService.getAllByBooker(2L, "ALL")).containsExactly(booking);
        assertThat(bookingService.getAllByBooker(2L, "CURRENT")).containsExactly(booking);
        assertThat(bookingService.getAllByBooker(2L, "PAST")).containsExactly(booking);
        assertThat(bookingService.getAllByBooker(2L, "FUTURE")).containsExactly(booking);
        assertThat(bookingService.getAllByBooker(2L, "WAITING")).containsExactly(booking);
        assertThat(bookingService.getAllByBooker(2L, "REJECTED")).containsExactly(booking);
        assertThrows(ValidationException.class, () -> bookingService.getAllByBooker(2L, "UNKNOWN"));
    }

    @Test
    void getAllByOwner() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(1L, "ALL"));
    }

    private BookingCreateDto createBookingCreateDto(Long itemId) {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User" + id);
        user.setEmail("user" + id + "@mail.com");
        return user;
    }

    private Item createItem(Long id, Boolean available, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(available);
        item.setOwner(owner);
        return item;
    }

    private Booking createBooking(Long id, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }
}
