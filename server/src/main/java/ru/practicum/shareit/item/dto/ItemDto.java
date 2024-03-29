package ru.practicum.shareit.item.dto;

import lombok.*;

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
    private String name;
    private String description;
    private Boolean available; // статус вещи, true - доступна
    private Long requestId;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments = new ArrayList<>();

    public ItemDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

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
}
