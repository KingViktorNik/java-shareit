package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class ItemRequestServiceImplTest {
    private final EntityManager em;
    private final ItemRequestServiceImpl itemRequestService;
    private final UserService userService;

    private User user;
    private final LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    @BeforeEach
    void setUp() {
        UserDto userDto = userService.newUser(new UserDto(null, "user@mail.com", "user"));
        user = em.createQuery("SELECT u FROM User u WHERE u.id=:id", User.class)
                .setParameter("id", userDto.getId())
                .getSingleResult();
    }

    @AfterEach
    void setClear() {
        em.createQuery("DELETE FROM User ");
        em.createQuery("DELETE FROM Item ");
        em.createQuery("DELETE FROM ItemRequest ");
    }

    @Test
    void addItemRequest() {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "description", dateTime, List.of());

        // when
        ItemRequestDto itemRequestDtoTest = itemRequestService.addItemRequest(user.getId(), itemRequestDto);

        // then
        ItemRequest itemRequest = em.createQuery("SELECT r FROM ItemRequest r WHERE r.id=:id", ItemRequest.class)
                .setParameter("id", itemRequestDtoTest.getId())
                .getSingleResult();

        assertThat(itemRequestDtoTest.getId(), notNullValue());
        assertThat(itemRequestDtoTest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestDtoTest.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void getAllRequestsByUser() {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "description", dateTime, List.of());
        itemRequestDto = itemRequestService.addItemRequest(user.getId(), itemRequestDto);

        // when
        List<ItemRequestDto> itemRequestsDtoTest = itemRequestService.getAllRequestsByUser(user.getId());

        // then
        List<ItemRequestDto> itemRequests = List.of(itemRequestDto);

        assertThat(itemRequestsDtoTest, hasSize(1));
        assertThat(itemRequestsDtoTest.get(0).getId(), notNullValue());
        assertThat(itemRequestsDtoTest.get(0).getDescription(), equalTo(itemRequests.get(0).getDescription()));
        assertThat(itemRequestsDtoTest.get(0).getCreated(), equalTo(itemRequests.get(0).getCreated()));
    }

    @Test
    void getAllRequests() {
        // given
        UserDto userDto = userService.newUser(new UserDto(null, "userNew@mail.com", "userNew"));
        User userNew = em.createQuery("SELECT u FROM User u WHERE u.id=:id", User.class)
                .setParameter("id", userDto.getId())
                .getSingleResult();

        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "description", dateTime, List.of());
        itemRequestDto = itemRequestService.addItemRequest(user.getId(), itemRequestDto);

        // when
        List<ItemRequestDto> itemRequestsDtoTest = itemRequestService.getAllRequests(userNew.getId(), 0, 1);

        // then
        List<ItemRequestDto> itemRequests = List.of(itemRequestDto);

        assertThat(itemRequestsDtoTest, hasSize(1));
        assertThat(itemRequestsDtoTest.get(0).getId(), notNullValue());
        assertThat(itemRequestsDtoTest.get(0).getDescription(), equalTo(itemRequests.get(0).getDescription()));
        assertThat(itemRequestsDtoTest.get(0).getCreated(), equalTo(itemRequests.get(0).getCreated()));
    }

    @Test
    void getRequest() {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "description", dateTime, List.of());
        itemRequestDto = itemRequestService.addItemRequest(user.getId(), itemRequestDto);

        // when
        ItemRequestDto itemRequestsDtoTest = itemRequestService.getRequest(user.getId(), itemRequestDto.getId());

        // then
        ItemRequest itemRequest = em.createQuery("SELECT ir FROM ItemRequest ir WHERE ir.id=:id", ItemRequest.class)
                .setParameter("id", itemRequestsDtoTest.getId())
                .getSingleResult();

        assertThat(itemRequestsDtoTest.getId(), notNullValue());
        assertThat(itemRequestsDtoTest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestsDtoTest.getCreated(), equalTo(itemRequest.getCreated()));
    }
}