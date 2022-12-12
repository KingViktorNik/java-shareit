package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return new ItemRequest(null,
                itemRequestDto.getDescription(),
                itemRequestDto.getCreated(),
                null
        );
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                List.of()
        );
    }

    public static ItemRequestDto.Item toDto(Item item) {
        return new ItemRequestDto.Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public static List<ItemRequestDto.Item> toDtoListItem(List<Item> item) {
        if (item == null) {
            return List.of();
        }
        return item.stream()
                .map(ItemRequestMapper::toDto)
                .collect(toList());
    }
}
