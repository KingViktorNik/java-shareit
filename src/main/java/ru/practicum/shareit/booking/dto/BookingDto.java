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
}



