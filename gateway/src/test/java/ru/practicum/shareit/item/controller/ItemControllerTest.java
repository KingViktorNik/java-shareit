package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private Map<String, Object> itemDto;
    private final Instant dateTime = Instant.now();

    @BeforeEach
    void setUp() {
        itemDto = new HashMap<>();
        itemDto.put("id", 1L);
        itemDto.put("name", "item");
        itemDto.put("description", "description");
        itemDto.put("available", true);
    }

    @Test
    void addItemUserIdNull() throws Exception {
        when(itemClient.addItem(anyLong(), any(ItemDto.class)))
                .thenThrow(ConflictException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(409));
    }

    @Test
    void addItem() throws Exception {
        ItemDto.Comment comment = new ItemDto.Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setAuthorName("User1");
        comment.setCreated(dateTime);

        ItemDto.Booking booking = new ItemDto.Booking();
        booking.setId(1L);
        booking.setBookerId(1L);
        booking.setStart(LocalDateTime.now().plusMinutes(1));
        booking.setEnd(LocalDateTime.now().plusMinutes(2));

        itemDto.put("comments", List.of(comment));
        itemDto.put("lastBooking", booking);
        when(itemClient.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.get("id")), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.get("name"))))
                .andExpect(jsonPath("$.available", is(itemDto.get("available"))))
                .andExpect(jsonPath("$.description", is(itemDto.get("description"))));
    }

    @Test
    void addItemNotUser() throws Exception {
        when(itemClient.addItem(anyLong(), any()));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void updateItem() throws Exception {
        when(itemClient.updateItem(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.get("id")), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.get("name"))))
                .andExpect(jsonPath("$.available", is(itemDto.get("available"))))
                .andExpect(jsonPath("$.description", is(itemDto.get("description"))));
    }

    @Test
    void getByItemId() throws Exception {
        when(itemClient.getByItemId(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.get("id")), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.get("name"))))
                .andExpect(jsonPath("$.available", is(itemDto.get("available"))))
                .andExpect(jsonPath("$.description", is(itemDto.get("description"))));
    }

    @Ignore
    void getUserItemAll() throws Exception {
        when(itemClient.getByUserIdItemAll(any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.get("id")), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.get("name"))))
                .andExpect(jsonPath("$[0].available", is(itemDto.get("available"))))
                .andExpect(jsonPath("$[0].description", is(itemDto.get("description"))));
    }

    @Test
    void getItemSearch() throws Exception {
        when(itemClient.getItemSearch(any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=script")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.get("id")), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.get("name"))))
                .andExpect(jsonPath("$[0].available", is(itemDto.get("available"))))
                .andExpect(jsonPath("$[0].description", is(itemDto.get("description"))));
    }

    @Test
    void addComment() throws Exception {
        Map<String, Object> commentDto = Map.of("text", "comment");
        when(itemClient.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.get("text"))));
    }
}