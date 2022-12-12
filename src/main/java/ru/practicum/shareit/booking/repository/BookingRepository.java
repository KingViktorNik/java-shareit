package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /** Все арендованные вещи пользователя(владельца) **/
    Page<Booking> findAllByBooker_Id(Pageable pageable, Long userId);

    /** Все вещи пользователя **/
    Page<Booking> findAllByItem_OwnerId(Pageable pageable,
                                        Long userId);

    /** Все вещи которые пользователь хочет арендовать в будущем **/
    Page<Booking> findAllByBooker_IdAndStartDateIsAfter(Pageable pageable, Long userId, LocalDateTime dateTime);

    /** Все вещи которые хотят арендовать у владельца вещей **/
    Page<Booking> findAllByItem_OwnerIdAndStartDateAfter(Pageable pageable, Long userId, LocalDateTime dateTime);

    /** Фильтр по статусу владельца вещей. **/
    Page<Booking> findAllByStatusAndItem_OwnerId(Pageable pageable, StatusBooking status, Long userId);

    /** Список пользователя завершенных бронирований  **/
    Page<Booking> findAllByBooker_IdAndEndDateBefore(Pageable pageable, Long userId, LocalDateTime dateTime);

    /** Список владельца вещей завершенных бронирований **/
    Page<Booking> findAllByItem_OwnerIdAndEndDateBefore(Pageable pageable, Long userId, LocalDateTime dateTime);

    /** проверка арендовал ли пользователь вещ **/
    @Query("from Booking b " +
            "where b.status in('APPROVED','PAST', 'CURRENT') " +
            "and b.booker.id = :userId " +
            "and b.item.id = :itemId " +
            "and b.endDate < :dateTime")
    List<Booking> commentByBookerId(@Param("userId") Long userId,
                                    @Param("itemId") Long itemId,
                                    @Param("dateTime") LocalDateTime dateTime);

    /** Поиск вещи по id и id пользователя или id владельца вещи **/
    @Query("from Booking " +
            "where id = :bookingId " +
            "and (booker.id = :userId " +
            "or item.ownerId = :userId" +
            ")")
    Optional<Booking> getByBooker(@Param("userId") Long userId,
                                  @Param("bookingId") Long bookingId);

    /** Список всех вещей арендованных пользователем по заданному статусу **/
    @Query("from Booking b " +
            "where (b.booker.id = :userId " +
            "and b.status = :status ) " +
            "order by b.startDate desc ")
    Page<Booking> getBookingStatusByUser(Pageable pageable,
                                         @Param("userId") Long userId,
                                         @Param("status") StatusBooking status);

    /** Список вещей ожидающие подтверждения владельца **/
    @Query("from Booking  b " +
            "where b.status = 'WAITING' " +
            "and b.item.ownerId = :ownerId " +
            "order by b.startDate desc ")
    Page<Booking> getBookingWaitingByOwner(Pageable pageable,
                                           Long ownerId);

    /** Текущие заказы пользователя **/
    @Query("from Booking b " +
            "where b.booker.id = :userId " +
            "and b.startDate < :dateTime " +
            "and b.endDate > :dateTime")
    Page<Booking> getBookingCurrent(Pageable pageable,
                                    @Param("userId") Long userId,
                                    @Param("dateTime") LocalDateTime dateTime);

    /** Текущие заказы владельца вещей **/
    @Query("from Booking b " +
            "where b.item.ownerId = :userId " +
            "and b.startDate < :dateTime " +
            "and b.endDate > :dateTime " +
            "order by b.startDate desc ")
    Page<Booking> getBookingOwnerCurrent(Pageable pageable,
                                         @Param("userId") Long userId,
                                         @Param("dateTime") LocalDateTime dateTime);

    /** Список вещей отклоненных пользователем или владельцем **/
    @Query("from Booking b " +
            "where (b.booker.id = :userId " +
            "and b.status in('REJECTED', 'CANCELED')) " +
            "order by b.startDate")
    Page<Booking> getBookingRejected(Pageable pageable,
                                     @Param("userId") Long userId);

    /** Список вещей подтвенрённых владельцем **/
    @Query("from Booking b " +
            "where b.item.id = :itemId " +
            "and (b.status = 'APPROVED' " +
            "and (b.endDate > :end " +
            "and b.startDate > :start)" +
            ")")
    List<Booking> getBookingByDate(@Param("itemId") Long itemId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query("from Booking as b " +
            "where b.item.id = :itemId " +
            "and b.status = 'APPROVED' " +
            "and b.startDate < :dateTime " +
            "order by b.startDate desc ")
    List<Booking> getLastBooking(@Param("itemId") Long itemId,
                                 @Param("dateTime") LocalDateTime dateTime);

    @Query("from Booking as b " +
            "where b.endDate = (select max(b.endDate) from Booking as b)" +
            "and b.item.id = :itemId " +
            "and b.status = 'APPROVED'")
    Optional<Booking> getNextBooking(@Param("itemId") Long itemId);
}
