package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now, LocalDateTime current);

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    Collection<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now, LocalDateTime current);

    Collection<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus status);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime now);
}
