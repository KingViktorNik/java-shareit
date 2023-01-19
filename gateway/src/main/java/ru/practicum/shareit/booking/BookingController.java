package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/bookings",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class BookingController {
    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addBooking(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody BookingDto bookingDto) {

        // Дата аренды не должна быть в прошлом
        if (bookingDto.getStart().isBefore(LocalDateTime.now())
                // дата окончания аренды не должна быть меньше даты аренды
                || bookingDto.getEnd().isBefore(bookingDto.getStart())
                // дата начала аренды не должно быть позднее даты окончания аренды
                || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException(bookingDto.getEnd() + " It can't be in the past");
        }

        ResponseEntity<Object> result = ResponseEntity.ok(bookingClient.addBooking(userId, bookingDto));
        log.info("[POST:{}] addBooking[userId:{} bookingId:{}]",
                result.getStatusCode(),
                userId,
                ((Map) result.getBody()).get("id")
        );
        return result;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> statusBooking(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                @Valid @PathVariable Long bookingId,
                                                @NotBlank @RequestParam String approved) {
        ResponseEntity<Object> result = ResponseEntity.ok(bookingClient.statusBooking(userId, bookingId, approved));
        log.info("[PATCH:{}] statusBooking[userId:{} bookingId:{} approved:{}]",
                result.getStatusCode(),
                userId,
                bookingId,
                approved
        );
        return result;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                             @Valid @PathVariable(required = false) Long bookingId) {
        ResponseEntity<Object> result = ResponseEntity.ok(bookingClient.getBooking(userId, bookingId));
        Map<String, Object> body = (Map) result.getBody();
        log.info("[GET:{}] getBooking[bookingId:{} itemId:{}, bookerId:{}]",
                result.getStatusCode(),
                bookingId,
                ((Map) body.get("item")).get("id"),
                ((Map) body.get("booker")).get("id")
        );
        return result;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                        @RequestParam(required = false, name = "state", defaultValue = "ALL") String stateParam,
                                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        StatusBooking state = StatusBooking.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
        ResponseEntity<Object> result = ResponseEntity.ok(bookingClient.getAllBookingsByOwner(userId, state, from, size));
        log.info("[GET:{} getAllBookingsByOwner[ownerId:{} page:{} size:{}]]",
                result.getStatusCode(),
                userId,
                from,
                ((List) result.getBody()).size()
        );
        return result;
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUser(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                       @RequestParam(required = false, name = "state", defaultValue = "ALL") String stateParam,
                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        StatusBooking state = StatusBooking.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));

        ResponseEntity<Object> result = ResponseEntity.ok(bookingClient.getAllBookingsByUser(userId, state, from, size));
        log.info("[GET:{} getAllBookingsByUser[userId:{} page:{} size:{}]]",
                result.getStatusCode(),
                userId,
                from,
                ((List) result.getBody()).size()
        );
        return result;
    }
}
