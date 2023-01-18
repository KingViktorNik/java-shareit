package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class BookingControllerUnitTest {
    private BookingDto bookingDto;
    @Mock
    private BookingClient client;
    @InjectMocks
    private BookingController controller;

    @Test
    void addBookingNullUserId() {
        //given
        Long userId = null;
        bookingDto = new BookingDto();

        //when
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> controller.addBooking(userId, bookingDto)
        );

        //then
        Assertions.assertEquals("missing header data 'X-Sharer-User-Id'", exception.getMessage());
    }

    @Test
    void addBookingLeaseDateError() {
        //given
        Long userId = 1L;
        bookingDto = new BookingDto(null,
                LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS)
        );

        //when
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> controller.addBooking(userId, bookingDto)
        );

        //then
        Assertions.assertEquals(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS) +
                " It can't be in the past", exception.getMessage());
    }

    @Ignore
    void addBookingHappy() {
        //given
        Long userId = 1L;
        bookingDto = new BookingDto(null,
                LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS)
        );

//        when(client.addBooking(anyLong(), any(BookingInsertDto.class)))
//                .thenReturn(bookingDto);
        //when
        ResponseEntity<Object> result = controller.addBooking(userId, bookingDto);
        //then
        assertThat(((Map<String, Object>) result.getBody()).get("id"), equalTo(1L));

        verify(client, times(1)).addBooking(anyLong(), any(BookingDto.class));
        verifyNoMoreInteractions(client);
    }

    @Test
    void statusBooking() {
    }

    @Test
    void getBooking() {
    }

    @Test
    void getAllBookingsByOwner() {
    }

    @Test
    void getAllBookingsByUser() {
    }
}