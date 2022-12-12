package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInsertDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingInsertDto bookingInsertDto);

    BookingDto statusBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getByBooking(Long userId, Long bookingId);

    List<BookingDto> getAllByUserOwner(Long userId, String status, Integer from, Integer size);

    List<BookingDto> getAllBookingsByUser(Long userId, String status, Integer from, Integer size);
}
