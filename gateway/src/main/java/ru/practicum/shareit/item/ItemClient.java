package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public Object addItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto).getBody();
    }

    public Object updateItem(Long userId, ItemDto itemDto) {
        return patch("/" + itemDto.getId(), userId, itemDto).getBody();
    }

    public Object getByItemId(Long userId, Long itemId) {
        return get("/" + itemId, userId).getBody();
    }

    public Object getByUserIdItemAll(Long userId) {
        return get("", userId).getBody();
    }

    public Object getItemSearch(String search) {
        Map<String, Object> parameters = Map.of("search", search);
        return get("/search?text={search}", null, parameters).getBody();
    }

    public Object addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto).getBody();
    }
}
