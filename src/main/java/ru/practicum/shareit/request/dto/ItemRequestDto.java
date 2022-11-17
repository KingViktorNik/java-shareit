package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemRequestDto {

    private Long id;

    @NotNull(message = "can not be null")
    @NotBlank(message = "can not be empty")
    private String description;
}
