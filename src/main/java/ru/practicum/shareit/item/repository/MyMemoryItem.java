package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MyMemoryItem implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Long create(Item item) {
        item.setId(++id);
        items.put(id, item);
        return id;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item getById(Long id) {
        return items.get(id);
    }

    @Override
    public Item getByName(String name) {
        return items.values().stream()
                .filter(item -> item.getName().equals(name))
                .findFirst().orElse(null);
    }

    @Override
    public List<Item> getByUserIdItemAll(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemSearch(String search) {
        return items.values().stream()
                .filter(item -> (item.getDescription().toLowerCase()).contains(search) && item.getAvailable())
                .collect(Collectors.toList());
    }
}