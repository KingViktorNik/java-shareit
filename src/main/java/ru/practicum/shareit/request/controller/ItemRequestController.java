package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(value = "/requests",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemRequestDto> addItemRequest(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return ResponseEntity.ok(itemRequestService.addItemRequest(userId, itemRequestDto));
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByUser(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequestsByUser(userId);
    }

    @GetMapping ("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequest(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.getRequest(userId, requestId));
    }
}
