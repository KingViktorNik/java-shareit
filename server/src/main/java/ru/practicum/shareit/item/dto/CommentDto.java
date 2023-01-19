package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private Instant created;

    public CommentDto(String text) {
        this.text = text;
    }
}
