package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** Список всех комментариев пользователя(владельца) **/
    List<Comment> findAllByItem_Id(Long itemId);
}
