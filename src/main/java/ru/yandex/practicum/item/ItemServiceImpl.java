package ru.yandex.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.booking.Booking;
import ru.yandex.practicum.booking.BookingMapper;
import ru.yandex.practicum.booking.BookingRepository;
import ru.yandex.practicum.booking.BookingStatus;
import ru.yandex.practicum.comment.Comment;
import ru.yandex.practicum.comment.CommentRepository;
import ru.yandex.practicum.comment.dto.CommentCreateDto;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.exception.BadRequestException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Override
    public List<ItemDto> findByOwnerId(Long ownerId) {
        log.info("Поиск всех вещей владельца с ID: {}", ownerId);

        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found with id: " + ownerId);
        }

        return itemRepository.findByOwnerId(ownerId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(Long id) {
        log.info("Поиск вещи по ID: {}", id);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));

        return itemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        log.info("Создание вещи для пользователя с ID: {}", ownerId);

        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));

        validateItemDto(itemDto);

        Item item = itemMapper.toEntity(itemDto, user);
        Item savedItem = itemRepository.save(item);

        log.info("Создана вещь с ID: {}", savedItem.getId());
        return itemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        log.info("Обновление вещи ID: {} пользователем ID: {}", itemId, ownerId);

        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found with id: " + ownerId);
        }

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Item not found or user is not the owner");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        log.info("Обновлена вещь с ID: {}", updatedItem.getId());

        return itemMapper.toDto(updatedItem);
    }


    @Override
    public List<ItemDto> search(String text) {
        log.info("Поиск вещей по тексту: {}", text);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchAvailableItems(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> getUserItemsWithBookings(Long userId) {
        log.info("Получение всех вещей пользователя {} с информацией о бронированиях и комментариях", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();

        List<Comment> allComments = commentRepository.findByItemIdsOrderByCreatedAsc(itemIds);
        Map<Long, List<CommentDto>> commentsByItemId = allComments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(this::mapToCommentDto, Collectors.toList())
                ));

        List<Booking> lastBookings = bookingRepository.findLastBookingsForItemsNative(
                itemIds,
                BookingStatus.APPROVED.name(),
                now
        );

        Map<Long, Booking> lastBookingByItemId = lastBookings.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (existing, replacement) -> existing
                ));

        List<Booking> nextBookings = bookingRepository.findNextBookingsForItemsNative(
                itemIds,
                BookingStatus.APPROVED.name(),
                now
        );

        Map<Long, Booking> nextBookingByItemId = nextBookings.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (existing, replacement) -> existing
                ));

        return items.stream()
                .map(item -> buildItemResponseDto(
                        item,
                        commentsByItemId.getOrDefault(item.getId(), List.of()),
                        lastBookingByItemId.get(item.getId()),
                        nextBookingByItemId.get(item.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItemByIdWithBookings(Long userId, Long itemId) {
        log.info("Получение вещи ID: {} пользователем {}", itemId, userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        boolean isOwner = item.getOwner().getId().equals(userId);

        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedAsc(itemId)
                .stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();

        if (isOwner) {
            List<Booking> lastBooking = bookingRepository.findByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                    itemId, BookingStatus.APPROVED, now);

            List<Booking> nextBooking = bookingRepository.findByItemIdAndStatusAndStartAfterOrderByStartAsc(
                    itemId, BookingStatus.APPROVED, now);

            return buildItemResponseDto(
                    item,
                    comments,
                    lastBooking.isEmpty() ? null : lastBooking.get(0),
                    nextBooking.isEmpty() ? null : nextBooking.get(0)
            );
        } else {
            return buildItemResponseDtoWithoutBookings(item, comments);
        }
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        log.info("Добавление комментария пользователем {} к вещи {}", userId, itemId);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        boolean hasCompletedBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду или аренда ещё не завершена");
        }

        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.info("Добавлен комментарий ID: {} к вещи ID: {}", savedComment.getId(), itemId);

        return mapToCommentDto(savedComment);
    }


    private ItemResponseDto mapToResponseDtoWithBookingsAndComments(Item item, LocalDateTime now, boolean includeBookings) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedAsc(item.getId())
                .stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());
        dto.setComments(comments);


        if (includeBookings) {
            List<Booking> pastBookings = bookingRepository.findByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                    item.getId(), BookingStatus.APPROVED, now);

            if (!pastBookings.isEmpty()) {
                Booking lastBooking = pastBookings.get(0);
                dto.setLastBooking(bookingMapper.toBookingInfoDto(lastBooking));
            }

            List<Booking> futureBookings = bookingRepository.findByItemIdAndStatusAndStartAfterOrderByStartAsc(
                    item.getId(), BookingStatus.APPROVED, now);

            if (!futureBookings.isEmpty()) {
                Booking nextBooking = futureBookings.get(0);
                dto.setNextBooking(bookingMapper.toBookingInfoDto(nextBooking));
            }
        }

        return dto;
    }

    private ItemResponseDto mapToResponseDtoWithoutBookings(Item item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(null);
        dto.setNextBooking(null);

        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedAsc(item.getId())
                .stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());
        dto.setComments(comments);

        return dto;
    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new BadRequestException("Название вещи не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new BadRequestException("Описание вещи не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Статус доступности должен быть указан");
        }
    }

    private ItemResponseDto buildItemResponseDto(Item item, List<CommentDto> comments,
                                                 Booking lastBooking, Booking nextBooking) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setComments(comments);

        if (lastBooking != null) {
            dto.setLastBooking(bookingMapper.toBookingInfoDto(lastBooking));
        }

        if (nextBooking != null) {
            dto.setNextBooking(bookingMapper.toBookingInfoDto(nextBooking));
        }

        return dto;
    }

    private ItemResponseDto buildItemResponseDtoWithoutBookings(Item item, List<CommentDto> comments) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setComments(comments);
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        return dto;
    }


}