package ru.practicum.shareit.booking.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInsertDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(value = "/bookings",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestBody BookingInsertDto bookingInsertDto) {
        return ResponseEntity.ok(bookingService.addBooking(userId, bookingInsertDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> statusBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable Long bookingId,
                                                    @RequestParam Boolean approved) {
        return ResponseEntity.ok(bookingService.statusBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getByBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getByBooking(userId, bookingId));
    }


    @GetMapping("/owner")
    public List<BookingDto> getAllByUserOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam StatusBooking state,
                                              @RequestParam Integer from,
                                              @RequestParam Integer size) {
        return bookingService.getAllByUserOwner(userId, state, from, size);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam StatusBooking state,
                                                 @RequestParam Integer from,
                                                 @RequestParam Integer size) {
        return bookingService.getAllBookingsByUser(userId, state, from, size);
    }
}
