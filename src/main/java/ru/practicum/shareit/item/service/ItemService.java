package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto);

    ItemDto getByItemId(Long itemId);

    List<ItemDto> getByUserIdItemAll(Long userId);

    List<ItemDto> getItemSearch(String search);
}