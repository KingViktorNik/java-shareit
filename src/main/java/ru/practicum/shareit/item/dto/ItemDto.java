package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotNull(message = "can not be null")
    @NotBlank(message = "can not be empty")
    private String name;

    @NotNull(message = "can not be null")
    @NotBlank(message = "can not be empty")
    private String description;

    @NotNull(message = "can not be null")
    private Boolean available; // статус вещи, true - доступна

    private BookingItemDto lastBooking;

    private BookingItemDto nextBooking;

    private List<CommentDto> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDto)) return false;
        return id != null && id.equals(((ItemDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
