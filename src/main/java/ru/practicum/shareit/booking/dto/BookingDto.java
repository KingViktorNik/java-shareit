package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private Map<String, Long> booker = new LinkedHashMap<>();
    private Map<String, Object> item = new LinkedHashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingDto)) return false;
        return id != null && id.equals(((BookingDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void setBooker(Long bookerId) {
        booker.put("id", bookerId);
    }

    public void setItem(Long itemId, String itemName) {
        item.put("id", itemId);
        item.put("name", itemName);
    }
}



