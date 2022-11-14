package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingItemDto {
    private Long id;
    private Long bookerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingItemDto)) return false;
        return id != null && id.equals(((BookingItemDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
