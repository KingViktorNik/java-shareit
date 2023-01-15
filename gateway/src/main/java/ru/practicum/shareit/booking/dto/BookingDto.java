package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

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
        if (o == null || getClass() != o.getClass()) return false;
        BookingDto that = (BookingDto) o;
        return Objects.equals(id, that.id) && Objects.equals(start, that.start) && Objects.equals(end, that.end)
                && Objects.equals(status, that.status) && Objects.equals(booker, that.booker) && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, status, booker, item);
    }
}



