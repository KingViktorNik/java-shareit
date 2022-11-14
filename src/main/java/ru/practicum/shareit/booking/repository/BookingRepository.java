package ru.practicum.shareit.booking.repository;

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
    List<Booking> findAllByBooker_Id(Long userId);

    /** Все вещи пользователя **/
    List<Booking> findAllByItem_Owner_Id(Long userId);

    /** Все вещи которые пользователь хочет арендовать в будущем **/
    List<Booking> findAllByBooker_IdAndStartDateIsAfter(Long userId, LocalDateTime dateTime);

    /** Все вещи которые хотят арендовать у владельца вещей **/
    List<Booking> findAllByItem_Owner_IdAndEndDateAfter(Long userId, LocalDateTime dateTime);

    /** Фильтр по статусу владельца вещей. **/
    List<Booking> findAllByStatusAndItem_Owner_Id(StatusBooking status, Long userId);

    /** Список пользователя завершенных бронирований  **/
    List<Booking> findAllByBooker_IdAndEndDateBefore(Long userId, LocalDateTime dateTime);

    /** Список владельца вещей завершенных бронирований **/
    List<Booking> findAllByItem_Owner_IdAndEndDateBefore(Long userId, LocalDateTime dateTime);


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
            "or item.owner.id = :userId" +
            ")")
    Optional<Booking> getByBooker(@Param("userId") Long userId,
                                  @Param("bookingId") Long bookingId);

    /** Список всех вещей арендованных пользователем по заданному статусу **/
    @Query("from Booking b " +
            "where (b.booker.id = :userId " +
            "and b.status = :status ) " +
            "order by b.startDate")
    List<Booking> getBookingStatusByUser(@Param("userId") Long userId,
                                         @Param("status") StatusBooking status);

    /** Список вещей ожидающие подтверждения владельца **/
    @Query("from Booking  b " +
            "where b.status = 'WAITING' " +
            "and b.item.owner.id = :ownerId ")
    List<Booking> getBookingWaitingByOwner(Long ownerId);

    /** Текущие заказы пользователя **/
    @Query("from Booking b " +
            "where b.booker.id = :userId " +
            "and b.startDate < :dateTime " +
            "and b.endDate > :dateTime")
    List<Booking> getBookingCurrent(@Param("userId") Long userId,
                                    @Param("dateTime") LocalDateTime dateTime);

    /** Текущие заказы владельца вещей **/
    @Query("from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.startDate < :dateTime " +
            "and b.endDate > :dateTime")
    List<Booking> getBookingOwnerCurrent(@Param("userId") Long userId,
                                         @Param("dateTime") LocalDateTime dateTime);

    /** Список вещей отклоненных пользователем или владельцем **/
    @Query("from Booking b " +
            "where (b.booker.id = :userId " +
            "and b.status in('REJECTED', 'CANCELED')) " +
            "order by b.startDate")
    List<Booking> getBookingRejected(@Param("userId") Long userId);

    /** Список вещей подтворённых владельцем **/
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
