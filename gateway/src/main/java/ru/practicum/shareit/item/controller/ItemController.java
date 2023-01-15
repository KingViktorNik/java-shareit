package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/items",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ItemController {
    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addItem(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        ResponseEntity<Object> result = ResponseEntity.ok(itemClient.addItem(userId, itemDto));
        Map<String, Object> body = (Map) result.getBody();
        log.info("[POST:{}] add items[userId:{} itemId:{}]",
                result.getStatusCode(),
                userId,
                body.get("id")
        );
        return result;
    }

    @PatchMapping(path = "/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateItem(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        itemDto.setId(itemId);
        ResponseEntity<Object> result = ResponseEntity.ok(itemClient.updateItem(userId, itemDto));
        log.info("[PATCH:{}] update items[userId:{} itemId:{}]",
                result.getStatusCode(),
                userId,
                itemDto.getId()
        );
        return result;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByItemId(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                              @Valid @PathVariable Long itemId) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        ResponseEntity<Object> result = ResponseEntity.ok(itemClient.getByItemId(userId, itemId));
        log.info("[GET:{}] update items[userId:{} itemId:{}]",
                result.getStatusCode(),
                userId,
                itemId
        );
        return result;
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemAll(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        ResponseEntity<Object> result = ResponseEntity.ok(itemClient.getByUserIdItemAll(userId));
        log.info("[GET:{}] all items[userId:{} size:{}]",
                result.getStatusCode(),
                userId,
                ((List) result.getBody()).size()
        );
        return result;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemSearch(@RequestParam("text") String search) {
        ResponseEntity<Object> result = ResponseEntity.ok(itemClient.getItemSearch(search));
        log.info("[GET:{}] item search[text:{} size:{}]",
                result.getStatusCode(),
                search,
                ((List) result.getBody()).size()
        );
        return result;
    }

    @PostMapping(path = "{itemId}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addComment(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        ResponseEntity<Object> result = ResponseEntity.ok(itemClient.addComment(userId, itemId, commentDto));
        log.info("[POST:{}] add comment[authorId:{} commentId:{}]",
                result.getStatusCode(),
                userId,
                ((Map<String, Object>) result.getBody()).get("id")
        );
        return ResponseEntity.ok(result.getBody());
    }
}
