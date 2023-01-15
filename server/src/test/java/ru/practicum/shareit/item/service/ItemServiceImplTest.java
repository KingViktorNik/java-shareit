package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        UserDto userDto = userService.newUser(new UserDto(null, "User", "user@email.com"));
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id=:id", User.class);
        user = query.setParameter("id", userDto.getId())
                .getSingleResult();
        item = new Item(null, "item1", "itemDescription1", true, user.getId(), null);
        itemDto = new ItemDto(null, "item1", "itemDescription1", true, null, null, null, null);
    }

    @AfterEach
    void setClear() {
        em.createQuery("DELETE FROM User u where u.name = 'User'");
        em.createQuery("DELETE FROM Item i where i.description = 'itemDescription1'");
    }

    @Test
    void addItem() {
        // when
        ItemDto itemDtoTest = itemService.addItem(user.getId(), itemDto);

        // then
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item itemTest = query.setParameter("id", itemDtoTest.getId())
                .getSingleResult();

        assertThat(itemDtoTest.getId(), notNullValue());
        assertThat(itemDtoTest.getName(), equalTo(itemTest.getName()));
        assertThat(itemDtoTest.getDescription(), equalTo(itemTest.getDescription()));
    }

    @Test
    void updateItem() {
        // given
        itemDto = itemService.addItem(user.getId(), itemDto);
        itemDto.setName("UserNameNew");

        // when
        ItemDto itemDtoTest = itemService.updateItem(user.getId(), itemDto);

        // then
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item itemTest = query.setParameter("id", itemDtoTest.getId())
                .getSingleResult();

        assertThat(itemDtoTest.getId(), notNullValue());
        assertEquals(itemDtoTest.getName(), "UserNameNew");
        assertThat(itemDtoTest.getDescription(), equalTo(itemTest.getDescription()));
        assertThat(itemDtoTest.getAvailable(), equalTo(itemTest.getAvailable()));
    }

    @Test
    void getByItemId() {
        //given
        itemDto = itemService.addItem(user.getId(), itemDto);

        // when
        ItemDto itemDtoTest = itemService.getByItemId(user.getId(), itemDto.getId());

        // then
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item itemTest = query.setParameter("id", itemDtoTest.getId())
                .getSingleResult();

        assertThat(itemDtoTest.getId(), notNullValue());
        assertThat(itemDtoTest.getId(), equalTo(itemTest.getId()));
        assertThat(itemDtoTest.getName(), equalTo(itemTest.getName()));
        assertThat(itemDtoTest.getDescription(), equalTo(itemTest.getDescription()));
        assertThat(itemDtoTest.getAvailable(), equalTo(itemTest.getAvailable()));
    }
}