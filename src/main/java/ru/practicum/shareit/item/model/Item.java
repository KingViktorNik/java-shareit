package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available; // статус вещи, true - доступна
    private Long owner; // идентификатор владельца вещи
}
