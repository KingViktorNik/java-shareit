package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
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
    return new Item(itemDto.getId(),                                                // id
                itemDto.getName(),                                                  // name
                itemDto.getDescription(),                                           // description
                itemDto.getAvailable(),                                             // available
                null,                                                               // owner
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null      // requestId
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),                                    // id
                item.getName(),                                             // name
                item.getDescription(),                                      // description
                item.getAvailable(),                                        // available
                item.getRequestId() != null ? item.getRequestId() : null,   // requesterId
                null,                                                       // lastBooking
                null,                                                       // nextBooking
                null                                                        // comments
        );
    }

    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequestId() != null ? item.getRequestId() : null);

        if (comments != null && comments.size() != 0) {
            itemDto.setComments(comments.stream()
                    .map(ItemMapper::toDtoCommentItem)
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
        itemDto.setRequestId(item.getRequestId() != null ? item.getRequestId() : null);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setComments(List.of());

        if (lastBooking != null) {
            ItemDto.Booking lastBookingDto = new ItemDto.Booking();

            lastBookingDto.setId(lastBooking.getId());
            lastBookingDto.setBookerId(lastBooking.getBooker().getId());
            lastBookingDto.setStart(lastBooking.getStartDate());
            lastBookingDto.setEnd(lastBooking.getEndDate());

            itemDto.setLastBooking(lastBookingDto);
        }

        if (nextBooking != null) {
            ItemDto.Booking nextBookingDto = new ItemDto.Booking();

            nextBookingDto.setId(nextBooking.getId());
            nextBookingDto.setBookerId(nextBooking.getBooker().getId());
            nextBookingDto.setStart(nextBooking.getStartDate());
            nextBookingDto.setEnd(nextBooking.getEndDate());

            itemDto.setNextBooking(nextBookingDto);
        }

        if (comments != null && comments.size() != 0) {
            itemDto.setComments(comments.stream()
                    .map(ItemMapper::toDtoCommentItem)
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

    private static ItemDto.Comment toDtoCommentItem(Comment comment) {
        ItemDto.Comment commentDto = new ItemDto.Comment();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getUser().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}