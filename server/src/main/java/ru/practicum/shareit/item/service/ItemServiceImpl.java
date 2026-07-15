package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public Item save(ItemDto itemDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Item item = ItemMapper.toItem(itemDto);

        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Статус доступности должен быть указан");
        }

        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id=" + itemDto.getRequestId() + " не найден")));
        }

        item.setOwner(user);

        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item update(ItemDto itemDto, Long userId, Long itemId) {

        Item changedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id=" + itemId + " не найдено"));
        Item item = ItemMapper.toItem(itemDto);
        if (!changedItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только владелец");
        }

        if (item.getName() != null) {
            changedItem.setName(item.getName());
        }

        if (item.getAvailable() != null) {
            changedItem.setAvailable(item.getAvailable());
        }

        if (item.getDescription() != null) {
            changedItem.setDescription(item.getDescription());
        }

        return changedItem;
    }

    @Override
    public ItemWithDatesDto findById(Long id) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещи с id=" + id + " не найдено"));

        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;

        if (item.getOwner().getId().equals(id)) {
            lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(item.getId(), LocalDateTime.now(), BookingStatus.APPROVED).map(Booking::getEnd).orElse(null);
            nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(), LocalDateTime.now(), BookingStatus.APPROVED).map(Booking::getStart).orElse(null);
        }

        List<CommentDto> comments = commentRepository.findAllByItemIdOrderByCreatedAsc(item.getId()).stream().map(CommentMapper::toCommentDto).toList();

        return ItemMapper.toItemWithDatesDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Collection<ItemWithDatesDto> findAll(Long userId) {

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> {
                    LocalDateTime lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(item.getId(), LocalDateTime.now(), BookingStatus.APPROVED).map(Booking::getEnd).orElse(null);
                    LocalDateTime nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(), LocalDateTime.now(),BookingStatus.APPROVED).map(Booking::getStart).orElse(null);
                    List<CommentDto> comments = commentRepository.findAllByItemIdOrderByCreatedAsc(item.getId()).stream().map(CommentMapper::toCommentDto).toList();
                    return ItemMapper.toItemWithDatesDto(item, lastBooking, nextBooking, comments);
                }).toList();
    }

    @Override
    public Collection<Item> findByText(String description) {
        if (description == null || description.isBlank()) {
            return List.of();
        }
        return itemRepository.search(description);
    }

    @Override
    public Comment addComment(Long itemId, Long userId, CommentDto commentDto) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id=" + itemId + " не найдено"));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым");
        }

        boolean volidateComment = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!volidateComment) {
            throw new ValidationException("Оставить комментарий можно только после завершённого бронирования");
        }

        Comment comment = CommentMapper.toComment(commentDto);

        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }


}
