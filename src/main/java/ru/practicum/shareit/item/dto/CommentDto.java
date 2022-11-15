package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    @Null
    private Long id;

    @NotNull(message = "can not be null")
    @NotBlank(message = "can not be empty")
    private String text;

    @Null
    private String authorName;

    @Null
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
