package ru.practicum.shareit.booking.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInsertDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingDto> addBooking(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody BookingInsertDto bookingInsertDto) {
        return ResponseEntity.ok(bookingService.addBooking(userId, bookingInsertDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> statusBooking(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                    @NotBlank @PathVariable Long bookingId,
                                                    @NotBlank @RequestParam Boolean approved) {
        return ResponseEntity.ok(bookingService.statusBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getByBooking(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                   @PathVariable(required = false) Long bookingId) {
        return ResponseEntity.ok(bookingService.getByBooking(userId, bookingId));
    }


    @GetMapping("/owner")
    public List<BookingDto> getAllByUserOwner(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                              @RequestParam(required = false, name = "state", defaultValue = "ALL") String status) {
        return bookingService.getAllByUserOwner(userId, status);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUser(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                 @RequestParam(required = false, name = "state", defaultValue = "ALL") String status) {
        return bookingService.getAllBookingsByUser(userId, status);
    }
}
