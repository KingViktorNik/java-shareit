package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingInsertDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingInsertDto)) return false;
        return itemId != null && itemId.equals(((BookingInsertDto) o).getItemId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
