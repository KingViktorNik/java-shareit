package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository, ItemMapper itemMapper, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = itemMapper.toEntity(itemDto);

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
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto) {
        Item newItem = itemMapper.toEntity(itemDto);

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
        return itemMapper.toDto(newItem);
    }

    @Override
    public ItemDto getByItemId(Long itemId) {
        return itemMapper.toDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> getByUserIdItemAll(Long userId) {
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }
        return itemRepository.getByUserIdItemAll(userId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public List<ItemDto> getItemSearch(String search) {
        if (search.isEmpty() || search.isBlank()) {
            return List.of();
        }
        return itemRepository.getItemSearch(search.toLowerCase()).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
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