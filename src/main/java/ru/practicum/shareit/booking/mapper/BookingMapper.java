package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInsertDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {
    public Booking toEntity(BookingInsertDto bookingInsertDto) {
        return new Booking(-1L,
                bookingInsertDto.getStart(),
                bookingInsertDto.getEnd(),
                null,
                null,
                null
        );
    }

    public BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus().toString(),
                booking.getBooker(),
                booking.getItem()
        );
    }
}
