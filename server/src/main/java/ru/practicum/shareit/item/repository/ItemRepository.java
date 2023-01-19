package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    /** Список всех вещей пользователя **/
    List<Item> findAllByOwnerId(Long userId);

    /** поиск вещи по названию **/
    @Query("from Item i " +
            "where upper(i.name) like upper(:text) " +
            "or upper(i.description) like upper(:text)")
    List<Item> getItemByName(@Param("text") String text);

    List<Item> findAllByRequestId(Long requestId);
}