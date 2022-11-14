package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ItemMapper {
    public static Item toEntity(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null
        );
    }

    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        ItemDto itemDto = new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                List.of()
        );

        if (comments != null && comments.size() != 0) {
            itemDto.setComments(comments.stream()
                    .map(ItemMapper::toCommentDto)
                    .collect(toList())
            );
        }
        return itemDto;
    }

    public static ItemDto toItemDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setComments(List.of());

        if (lastBooking != null) {
            itemDto.setLastBooking(new BookingItemDto(lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getStartDate(),
                    lastBooking.getEndDate())
            );
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(new BookingItemDto(nextBooking.getId(),
                    nextBooking.getBooker().getId(),
                    lastBooking.getStartDate(),
                    lastBooking.getEndDate())
            );
        }
        if (comments != null && comments.size() != 0) {
            itemDto.setComments(comments.stream()
                    .map(ItemMapper::toCommentDto)
                    .collect(toList())
            );
        }
        return itemDto;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getUser().getName(),
                comment.getCreated()
        );
    }
}