package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_item", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    // статус вещи, true - доступна
    @Column(name = "is_available")
    private Boolean available;

    // владелиц вещи
    private Long ownerId;

    // потребность в вещи
    private Long requestId;
}
