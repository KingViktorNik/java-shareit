package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Item addItem(Long userId, Item item) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }

        // Проверка существует ли такой пользователь
        if (userRepository.getById(userId) == null) {
            throw new NullObjectException("User with id: " + userId + " not found");
        }

        // Проверка, есть ли такая вещь в списке
        if (itemRepository.getByName(item.getName()) != null) {
            throw new ConflictException("The '" + item.getName() + "' item is already on the list");
        }

        item.setOwner(userId);
        item.setId(itemRepository.create(item));
        log.info("new item - id:'{}' name:'{}'", item.getId(), item.getName());
        return item;
    }

    @Override
    public Item updateItem(Long userId, Item newItem) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }

        User user = userRepository.getById(userId);

        // Проверка существует ли такой пользователь
        if (user == null) {
            throw new NullObjectException("User with id: " + userId + " not found");
        }

        // Список вещей пользователя
        List<Item> items = itemRepository.getByUserIdItemAll(userId);

        // Проверка, есть ли такая вещь в списке
        Item item = items.stream().filter(itemId -> itemId.getOwner().equals(userId)).findFirst().orElse(null);

        if (item == null) {
            throw new NullObjectException(String.format("User (id:%d name:%s) doesn't have a item(id:%d name:%s) thing",
                    user.getId(), user.getName(), newItem.getId(), newItem.getName()));
        }

        newItem = itemRepository.update(updateNullData(item, newItem));
        log.info("update item - id:'{}' name:'{}'", newItem.getId(), newItem.getName());
        return newItem;
    }

    @Override
    public Item getByItemId(Long itemId) {
        return itemRepository.getById(itemId);
    }

    @Override
    public List<Item> getByUserIdItemAll(Long userId) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        return itemRepository.getByUserIdItemAll(userId);
    }

    @Override
    public List<Item> getItemSearch(String search) {
        if (search.isEmpty() || search.isBlank()) {
            return List.of();
        }
        return itemRepository.getItemSearch(search.toLowerCase());
    }

    // Поверка полей на null
    private Item updateNullData(Item item, Item newItem) {

        if (newItem.getOwner() == null) {
            newItem.setOwner(item.getOwner());
        }

        if (newItem.getName() == null) {
            newItem.setName(item.getName());
        }

        if (newItem.getDescription() == null) {
            newItem.setDescription(item.getDescription());
        }

        if (newItem.getAvailable() == null) {
            newItem.setAvailable(item.getAvailable());
        }
        return newItem;
    }
}