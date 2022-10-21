package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUser implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public Long create(User user) {
        if (getByEmail(user.getEmail()) == null) {
            user.setId(++id);
            users.put(id, user);
            return id;
        }
        return null;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public User getByEmail(String email) {
        return users.values().stream()
                .filter(userEmail -> userEmail.getEmail().equals(email))
                .findFirst().orElse(null);
    }
}