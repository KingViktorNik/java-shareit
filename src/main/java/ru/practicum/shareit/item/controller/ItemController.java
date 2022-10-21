package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/items",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)

public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> addItem(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                           @Valid @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toEntity(itemDto);
        item = itemService.addItem(userId, item);
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @PatchMapping(path = "/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> updateItem(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        Item item = itemMapper.toEntity(itemDto);
        item = itemService.updateItem(userId, item);
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getByItemId(@Valid @PathVariable Long itemId) {
        Item item = itemService.getByItemId(itemId);
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @GetMapping
    public List<ItemDto> getUserItemAll(@Valid @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        List<Item> items = itemService.getByUserIdItemAll(userId);
        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> getItemSearch(@RequestParam("text") String search) {
        List<Item> items = itemService.getItemSearch(search);
        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}
