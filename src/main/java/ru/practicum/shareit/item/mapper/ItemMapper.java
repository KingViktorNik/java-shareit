package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                    .map(commentDto -> {
                        Map<String, Object> comment = new LinkedHashMap<>();
                        comment.put("id", commentDto.getId());
                        comment.put("text", commentDto.getText());
                        comment.put("authorName", commentDto.getUser().getName());
                        comment.put("created", commentDto.getCreated());
                        return comment;
                    })
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
        itemDto.setComments(List.of());

        if (lastBooking != null) {
            itemDto.setLastBooking(lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getStartDate(),
                    lastBooking.getEndDate());
        } else {
            itemDto.setLastBooking(null);
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(nextBooking.getId(),
                    nextBooking.getBooker().getId(),
                    nextBooking.getStartDate(),
                    nextBooking.getEndDate());
        } else {
            itemDto.setNextBooking(null);
        }
        if (comments != null && comments.size() != 0) {
            itemDto.setComments(comments.stream()
                    .map(commentDto -> {
                        Map<String, Object> comment = new LinkedHashMap<>();
                        comment.put("id", commentDto.getId());
                        comment.put("text", commentDto.getText());
                        comment.put("authorName", commentDto.getUser().getName());
                        comment.put("created", commentDto.getCreated());
                        return comment;
                    })
                    .collect(toList())
            );
        }
        return itemDto;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getUser().getName(),
                comment.getCreated()
        );
    }
}