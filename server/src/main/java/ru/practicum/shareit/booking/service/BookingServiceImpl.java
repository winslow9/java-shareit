package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    public final BookingRepository bookingRepository;
    public final UserRepository userRepository;
    public final ItemRepository itemRepository;

    @Transactional
    @Override
    public Booking save(BookingCreateDto bookingCreateDto, Long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + bookingCreateDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        Booking booking = BookingMapper.toBooking(bookingCreateDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Transactional
    @Override
    public Booking updateStatus(Long bookingId, Long userId, Boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));

        Long ownerId = booking.getItem().getOwner().getId();

        if (!ownerId.equals(userId)) {
            throw new ValidationException("Подтвердить бронирование может только владелец вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return booking;
    }

    @Override
    public Booking getById(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));

        User owner = booking.getItem().getOwner();
        User booker  = booking.getBooker();

        if (!(owner.getId().equals(userId) || booker.getId().equals(userId))) {
            throw new NotFoundException("Просматривать информацию может либо автор бронирования, либо владельц вещи,");
        }

        return booking;
    }

    @Override
    public Collection<Booking> getAllByBooker(Long userId, String state) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        LocalDateTime now = LocalDateTime.now();


        switch (state) {
            case "ALL" -> {
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            }
            case "CURRENT" -> {
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            }
            case "PAST" -> {
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            }
            case "FUTURE" -> {
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            }
            case "WAITING" -> {
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            }
            case "REJECTED" -> {
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            }
            default -> throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public Collection<Booking> getAllByOwner(Long ownerId, String state) {

        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id=" + ownerId + " не найден");
        }
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case "ALL" -> {
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            }
            case "CURRENT" -> {
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
            }
            case "PAST" -> {
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            }
            case "FUTURE" -> {
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            }
            case "WAITING" -> {
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            }
            case "REJECTED" -> {
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            }
            default -> throw new ValidationException("Unknown state: " + state);
        }
    }
}
