package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, Object> lastBooking = new LinkedHashMap<>();

    private Map<String, Object> nextBooking = new LinkedHashMap<>();

    private List<Map<String, Object>> comments;

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

    public void setLastBooking(Long itemId, Long bookerId, LocalDateTime start, LocalDateTime end) {
        lastBooking.put("id", itemId);
        lastBooking.put("bookerId", bookerId);
        lastBooking.put("start", start);
        lastBooking.put("end", end);
    }

    public void setNextBooking(Long itemId, Long bookerId, LocalDateTime start, LocalDateTime end) {
        nextBooking.put("id", itemId);
        nextBooking.put("bookerId", bookerId);
        nextBooking.put("start", start);
        nextBooking.put("end", end);
    }
}
