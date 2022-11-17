package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/items",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> addItem(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                           @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.addItem(userId, itemDto));
    }

    @PatchMapping(path = "/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> updateItem(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return ResponseEntity.ok(itemService.updateItem(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getByItemId(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                               @Valid @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getByItemId(userId, itemId));
    }

    @GetMapping
    public List<ItemDto> getUserItemAll(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        return itemService.getByUserIdItemAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemSearch(@RequestParam("text") String search) {
        return itemService.getItemSearch(search);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(itemService.addComment(userId, itemId, commentDto.getText()));
    }
}
