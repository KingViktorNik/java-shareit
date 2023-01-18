package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(value = "/requests",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addItemRequest(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        ResponseEntity<Object> result = ResponseEntity.ok(itemRequestClient.addItemRequest(userId, itemRequestDto));
        log.info("[POST:{}] addItemRequest[userId:{} itemRequestId:{}]",
                result.getStatusCode(),
                userId,
                ((Map<String, Object>) result.getBody()).get("id")
        );
        return result;
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUser(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        ResponseEntity<Object> result = ResponseEntity.ok(itemRequestClient.getAllRequestsByUser(userId));
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        log.info("[GET:{}] all request[userId:{} size:{}]",
                result.getStatusCode(),
                userId,
                ((List) result.getBody()).size()
        );
        return result;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        ResponseEntity<Object> result = ResponseEntity.ok(itemRequestClient.getAllRequests(userId, from, size));
        log.info("[GET:{}] all request[userId:{} page:{} size:{}]",
                result.getStatusCode(),
                userId,
                from,
                ((List) result.getBody()).size()
        );
        return result;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        ResponseEntity<Object> result = ResponseEntity.ok(itemRequestClient.getRequest(userId, requestId));
        log.info("[GET:{}] request[userId:{} requestId:{}]",
                result.getStatusCode(),
                userId,
                ((Map<String, Object>) result.getBody()).get("id")
        );
        return result;
    }
}
