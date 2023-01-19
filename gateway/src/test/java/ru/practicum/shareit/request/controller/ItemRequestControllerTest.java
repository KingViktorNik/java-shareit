package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestClient itemRequestClient;

    @Autowired
    private MockMvc mvc;

    private Map<String, Object> itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new HashMap<>();
        itemRequestDto.put("description", "item");
    }

    @Test
    void addItemRequestIdNotNull() throws Exception {
        itemRequestDto.put("id", 1L);
        when(itemRequestClient.addItemRequest(anyLong(), any()))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void addItemRequestIdNullDescription() throws Exception {
        itemRequestDto.put("description", null);
        when(itemRequestClient.addItemRequest(anyLong(), any()))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void addItemRequestIdBlankDescription() throws Exception {
        itemRequestDto.put("description", "");
        when(itemRequestClient.addItemRequest(anyLong(), any()))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void addItemRequestCreatedNotNull() throws Exception {
        itemRequestDto.put("created", LocalDateTime.now());
        when(itemRequestClient.addItemRequest(anyLong(), any()))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void addItemRequestCreatedUserIdInvalid() throws Exception {
        when(itemRequestClient.addItemRequest(anyLong(), any()))
                .thenThrow(NullPointerException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", anyLong()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }

    @Test
    void addItemRequest() throws Exception {
        itemRequestDto.put("id", null);
        itemRequestDto.put("created", null);
        when(itemRequestClient.addItemRequest(any(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.get("id")), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.get("created"))))
                .andExpect(jsonPath("$.description", is(itemRequestDto.get("description"))));
    }

    @Test
    void getAllRequestsByUserUserIdInvalid() throws Exception {
        when(itemRequestClient.getAllRequestsByUser(anyLong()))
                .thenThrow(ValidationException.class);

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", anyLong()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void getAllRequestsByUserUserIdNotFound() throws Exception {
        when(itemRequestClient.getAllRequestsByUser(anyLong()))
                .thenThrow(NullObjectException.class);

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", anyLong()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getAllRequestsByUser() throws Exception {
        itemRequestDto.put("id", 1L);
        List<ItemRequestDto.Item> items = List.of(
                new ItemRequestDto.Item(1L, "name", "description", true, 1L));
        itemRequestDto.put("items", items);

        List<Map> itemRequestsDto = List.of(itemRequestDto);
        when(itemRequestClient.getAllRequestsByUser(anyLong()))
                .thenReturn(itemRequestsDto);

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", anyLong())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.get("id")), Long.class))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.get("description"))));
    }

    @Test
    void getAllRequestsUserIdInvalid() throws Exception {
        when(itemRequestClient.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenThrow(ValidationException.class);

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 555))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));

    }

    @Test
    void getAllRequestsUserIdNotFound() throws Exception {
        when(itemRequestClient.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenThrow(NullObjectException.class);

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getAllRequests() throws Exception {
        itemRequestDto.put("id", 1L);
        List<Map> itemRequestsDto = List.of(itemRequestDto);

        when(itemRequestClient.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequestsDto);

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.get("id")), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.get("description"))));
    }

    @Test
    void getRequestUserIdInvalid() throws Exception {
        when(itemRequestClient.getRequest(anyLong(), anyLong()))
                .thenThrow(ValidationException.class);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 555))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));

    }

    @Test
    void getRequestUserIdNotFound() throws Exception {
        when(itemRequestClient.getRequest(anyLong(), anyLong()))
                .thenThrow(NullObjectException.class);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getRequest() throws Exception {
        itemRequestDto.put("id", 1L);
        when(itemRequestClient.getRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.get("id")), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.get("description"))));
    }
}