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
}
