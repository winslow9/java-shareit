package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
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
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void save() {
        User owner = createUser(1L);
        ItemDto itemDto = createItemDto("Дрель", "Аккумуляторная дрель", true);
        Item savedItem = createItem(1L, "Дрель", "Аккумуляторная дрель", true, owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        Item result = itemService.save(itemDto, 1L);

        assertThat(result).isEqualTo(savedItem);
        verify(userRepository).findById(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void saveWhenUserNotFound() {
        ItemDto itemDto = createItemDto("Дрель", "Описание", true);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.save(itemDto, 1L));
        verify(userRepository).findById(1L);
    }


    @Test
    void update() {
        User owner = createUser(1L);
        Item oldItem = createItem(1L, "Старая дрель", "Старое описание", true, owner);
        ItemDto itemDto = createItemDto("Новая дрель", "Новое описание", false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(oldItem));

        Item result = itemService.update(itemDto, 1L, 1L);

        assertThat(result.getName()).isEqualTo("Новая дрель");
        assertThat(result.getDescription()).isEqualTo("Новое описание");
        assertThat(result.getAvailable()).isFalse();

        verify(itemRepository).findById(1L);
    }

    @Test
    void updateWhenItemNotFound() {
        ItemDto itemDto = createItemDto("Новая дрель", "Новое описание", false);

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(itemDto, 1L, 1L));
    }

    @Test
    void updateWhenUserIsNotOwner() {
        User owner = createUser(1L);
        Item oldItem = createItem(1L, "Дрель", "Описание", true, owner);
        ItemDto itemDto = createItemDto("Новая дрель", "Новое описание", false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(oldItem));

        assertThrows(NotFoundException.class, () -> itemService.update(itemDto, 2L, 1L));
    }

    @Test
    void findById() {
        User owner = createUser(1L);
        Item item = createItem(1L, "Дрель", "Описание", true, owner);
        Booking lastBooking = createBooking(item, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        Booking nextBooking = createBooking(item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.of(nextBooking));
        when(commentRepository.findAllByItemIdOrderByCreatedAsc(1L)).thenReturn(List.of());

        ItemWithDatesDto result = itemService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Описание");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void findByIdWhenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findById(1L));
    }

    @Test
    void findAll() {
        User owner = createUser(1L);
        Item item = createItem(1L, "Дрель", "Описание", true, owner);

        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.empty());
        when(commentRepository.findAllByItemIdOrderByCreatedAsc(1L)).thenReturn(List.of());

        List<ItemWithDatesDto> result = itemService.findAll(1L).stream().toList();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getName()).isEqualTo("Дрель");
    }

    @Test
    void findByTextWhenTextIsNull() {
        assertThat(itemService.findByText(null)).isEmpty();
    }

    @Test
    void findByTextWhenTextIsBlank() {
        assertThat(itemService.findByText(" ")).isEmpty();
    }

    @Test
    void findByText() {
        User owner = createUser(1L);
        Item item = createItem(1L, "Дрель", "Описание", true, owner);

        when(itemRepository.search("дрель")).thenReturn(List.of(item));

        assertThat(itemService.findByText("дрель")).containsExactly(item);

        verify(itemRepository).search("дрель");
    }

    @Test
    void addComment() {
        User owner = createUser(1L);
        User author = createUser(1L);
        Item item = createItem(1L, "Дрель", "Описание", true, owner);
        CommentDto commentDto = createCommentDto("Отличная вещь");
        Comment savedComment = createComment(1L, "Отличная вещь", item, author);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(eq(1L), eq(1L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        Comment result = itemService.addComment(1L, 1L, commentDto);

        assertThat(result).isEqualTo(savedComment);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addCommentWhenItemNotFound() {
        CommentDto commentDto = createCommentDto("Отличная вещь");

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 2L, commentDto));
    }

    @Test
    void addCommentWhenUserNotFound() {
        User owner = createUser(1L);
        Item item = createItem(1L, "Дрель", "Описание", true, owner);
        CommentDto commentDto = createCommentDto("Отличная вещь");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 2L, commentDto));
    }


    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@mail.com");
        return user;
    }

    private ItemDto createItemDto(String name, String description, Boolean available) {
        return new ItemDto(null, name, description, available, null);
    }

    private Item createItem(Long id, String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return item;
    }

    private Booking createBooking(Item item, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(createUser(2L));
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    private CommentDto createCommentDto(String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);
        return commentDto;
    }

    private Comment createComment(Long id, String text, Item item, User author) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}