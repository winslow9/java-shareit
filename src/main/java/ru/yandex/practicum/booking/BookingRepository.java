package ru.yandex.practicum.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    // Все бронирования пользака
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    // Текущие бронирования
    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    // Завершённые бронирования
    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);

    // Будущие бронирования
    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

    // По статусу
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);


    // Все бронирования вещей владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId, Sort sort);

    // Текущие бронирования вещей владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start < :now AND b.end > :now")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    // Завершённые бронирования вещей владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :now")
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    // Будущие бронирования вещей владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > :now")
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    // По статусу для владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status")
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status, Sort sort);

    // Последнее завершённое бронирование
    List<Booking> findByItemIdAndStatusAndStartBeforeOrderByStartDesc(
            Long itemId, BookingStatus status, LocalDateTime now);

    // Ближайшее будущее бронирование
    List<Booking> findByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId, BookingStatus status, LocalDateTime now);


    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);

    // Поиск по ID с проверкой прав
    @Query("SELECT b FROM Booking b WHERE b.id = :id AND (b.booker.id = :userId OR b.item.owner.id = :userId)")
    Optional<Booking> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}