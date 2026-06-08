package ru.yandex.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.booking.dto.BookingDto;
import ru.yandex.practicum.booking.dto.BookingResponseDto;
import ru.yandex.practicum.exception.BadRequestException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.exception.AccessDeniedException;
import ru.yandex.practicum.item.Item;
import ru.yandex.practicum.item.ItemRepository;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    ;

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingDto bookingDto) {
        log.info("Создание бронирования пользователем {} для вещи {}", userId, bookingDto.getItemId());

        User booker = userService.getEntityById(userId);
        Item item = getItemById(bookingDto.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BadRequestException("Дата окончания должна быть позже даты начала");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала не может быть в прошлом");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Создано бронирование с ID: {}", savedBooking.getId());

        return bookingMapper.toResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("Подтверждение/отклонение бронирования {} пользователем {}. Approved: {}",
                bookingId, userId, approved);

        Booking booking = getBookingById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Только владелец может подтверждать бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Бронирование уже обработано. Текущий статус: " + booking.getStatus());
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Бронирование {} обновлено. Новый статус: {}", bookingId, updatedBooking.getStatus());

        return bookingMapper.toResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        log.info("Получение бронирования {} пользователем {}", bookingId, userId);

        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено или нет доступа"));

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, BookingState state) {
        log.info("Получение бронирований пользователя {} со статусом {}", userId, state);

        userService.getEntityById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                        userId, now, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBefore(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfter(userId, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, BookingState state) {
        log.info("Получение бронирований владельца {} со статусом {}", userId, state);

        userService.getEntityById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwnerId(userId, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findPastByOwnerId(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByOwnerId(userId, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID " + bookingId + " не найдено"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
    }
}