package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Long create(Item item);

    Item update(Item item);

    Item getById(Long id);

    Item getByName(String name);

    List<Item> getByUserIdItemAll(Long userId);

    List<Item> getItemSearch(String search);
}