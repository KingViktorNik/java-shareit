package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

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
    private final Booker booker = new Booker();
    private final Item item = new Item();

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Getter
    @Setter
    public static final class Booker {
        private Long id;

    }

    @Getter
    @Setter
    public static final class Item {
        private Long id;
        private String name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingDto)) return false;
        return id != null && id.equals(((BookingDto) o).getId());
    }
}



