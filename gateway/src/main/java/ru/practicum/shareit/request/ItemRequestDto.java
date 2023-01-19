package ru.practicum.shareit.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    @Null
    private Long id;

    @NotNull(message = "can not be null")
    @NotBlank(message = "can not be empty")
    private String description;

    @Null
    private LocalDateTime created;

    private List<Item> items = new ArrayList<>();

    public ItemRequestDto(String description) {
        this.description = description;
    }

    @Getter
    @Setter
    @AllArgsConstructor

    public static final class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
    }
}
