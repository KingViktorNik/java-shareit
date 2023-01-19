package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(@NotBlank(message = "missing header data 'X-Sharer-User-Id'") Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));

        Item item = ItemMapper.toEntity(itemDto);
        item.setOwnerId(user.getId());
        itemDto = ItemMapper.toItemDto(itemRepository.save(item));

        log.info("new item - id:'{}' name:'{}'", itemDto.getId(), itemDto.getName());

        return itemDto;
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto) {
        Long itemId = itemDto.getId();

        Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NullObjectException("Item with id:" + itemId + " not found"));

        if (!item.getOwnerId().equals(userId)) {
            throw new NullObjectException("The user with id:" + userId + " does not have such an item");
        }

        Item updateItem = ItemMapper.toEntity(itemDto);
        updateItem.setOwnerId(item.getOwnerId());

        if (updateItem.getAvailable() == null) {
            updateItem.setAvailable(item.getAvailable());
        }

        if (updateItem.getName() == null) {
            updateItem.setName(item.getName());
        }

        if (updateItem.getDescription() == null) {
            updateItem.setDescription(item.getDescription());
        }

        itemDto = ItemMapper.toItemDto(itemRepository.save(updateItem));

        log.info("update item - id:'{}' name:'{}'", updateItem.getId(), updateItem.getName());

        return itemDto;
    }

    @Override
    public ItemDto getByItemId(Long userId, Long itemId) {
        LocalDateTime dateTime = LocalDateTime.now();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NullObjectException("Thing with id:" + itemId + " does not exist"));

        List<Comment> comment = commentRepository.findAllByItem_Id(itemId);

        // Является ли пользователь владельцем вещи
        if (item.getOwnerId().equals(userId)) {
            Booking last = bookingRepository.getLastBooking(itemId, dateTime).stream()
                    .findFirst()
                    .orElse(null);

            Booking next = bookingRepository.getNextBooking(itemId)
                    .orElse(null);

            return ItemMapper.toItemDto(item, last, next, comment);
        }

        return ItemMapper.toItemDto(item, comment);
    }

    @Override
    public List<ItemDto> getByUserIdItemAll(Long userId) {
        LocalDateTime dateTime = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        return items.stream()
                .map(item -> {
                    if (item.getOwnerId().equals(userId)) {
                        Booking last = bookingRepository.getLastBooking(item.getId(), dateTime)
                                .stream()
                                .findFirst()
                                .orElse(null);

                        Booking next = bookingRepository.getNextBooking(item.getId())
                                .orElse(null);

                        List<Comment> comment = commentRepository.findAllByItem_Id(item.getId());

                        return ItemMapper.toItemDto(item, last, next, comment);
                    }

                    return ItemMapper.toItemDto(item);
                })
                .sorted((o1, o2) -> {
                    if (o1.getNextBooking() == null) {
                        return +1;
                    }
                    if (o2.getNextBooking() == null) {
                        return -1;
                    } else {
                        return LocalDateTime.parse(o1.getNextBooking().getStart().toString())
                                .compareTo(LocalDateTime.parse(o2.getNextBooking().getStart().toString()));
                    }
                })
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemSearch(String search) {
        if (search.isEmpty() || search.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.getItemByName("%" + search + "%");
        return items.stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = bookingRepository.commentByBookerId(userId, itemId, dateTime).stream()
                .findFirst()
                .orElse(null);

        if (booking == null) {
            throw new ValidationException("User with id:'" + userId + "' did not rent a thing with id:'" + itemId + "'");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(booking.getItem());
        comment.setUser(booking.getBooker());
        comment.setCreated(Instant.now());

        comment = commentRepository.save(comment);

        log.info("add comment itemId:'" + itemId + "' bookerId:'" + "' ");

        return ItemMapper.toCommentDto(comment);
    }
}

