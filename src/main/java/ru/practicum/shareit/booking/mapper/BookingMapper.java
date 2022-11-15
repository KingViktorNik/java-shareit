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
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStartDate());
        bookingDto.setEnd(booking.getEndDate());
        bookingDto.setStatus(booking.getStatus().toString());
        bookingDto.setBooker(booking.getBooker().getId());
        bookingDto.setItem(booking.getItem().getId(), booking.getItem().getName());

        return bookingDto;
    }
}
