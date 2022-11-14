package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    /** Список всех вещей пользователя **/
    List<Item> findAllByOwner_Id(Long userId);

    /** поиск вещи по названию **/
    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}