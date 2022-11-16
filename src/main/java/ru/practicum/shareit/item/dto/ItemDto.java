package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private Booking lastBooking;

    private Booking nextBooking;

    private List<Comment> comments = new ArrayList<>();

    @Getter
    @Setter
    public static final class Booking {
        private Long id;
        private Long bookerId;
        private LocalDateTime start;
        private LocalDateTime end;
    }

    @Getter
    @Setter
    public static final class Comment {
        private Long id;
        private String text;
        private String authorName;
        private Instant created;
    }
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
