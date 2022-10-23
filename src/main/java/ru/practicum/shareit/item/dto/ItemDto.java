package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    private Long id;

    @NotNull(message = "can not be null")
    @NotBlank(message = "can not be empty")
    private String name;

    @NotNull(message = "can not be null")
    @NotBlank(message = "can not be empty")
    private String description;

    @NotNull(message = "can not be null")
    private Boolean available; // статус вещи, true - доступна
}
