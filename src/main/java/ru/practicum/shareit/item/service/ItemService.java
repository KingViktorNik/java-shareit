package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Item item);

    Item getByItemId(Long itemId);

    List<Item> getByUserIdItemAll(Long userId);

    List<Item> getItemSearch(String search);
}