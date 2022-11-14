package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "the 'text' field cannot be empty")
    private String text;
    private String authorName;
    private Instant created;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentDto)) return false;
        return id != null && id.equals(((CommentDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
