package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class UserServiceImplTest {
    private final EntityManager em;
    private final UserService userService;

    @Test
    void newUser() {
        // given
        UserDto userDto = makeUserDto("user1@mail.com", "user1");

        // when
        userDto = userService.newUser(userDto);

        // then
        TypedQuery<User> query = em.createQuery("SELECT u from User u WHERE  u.id= :id", User.class);
        User user = query.setParameter("id", userDto.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getName(), equalTo(userDto.getName()));
    }

    @Test
    void userUpdate() {
        // given
        UserDto userDto = makeUserDto("user1@mail.com", "user1");
        userDto = userService.newUser(userDto);
        UserDto userDtoNew = makeUserDto("user2@mail.com", "user1");

        // when
        UserDto userDtoResult = userService.updateUser(userDtoNew, userDto.getId());

        // then
        TypedQuery<User> query = em.createQuery("SELECT u from User u WHERE  u.id= :id", User.class);
        User user = query.setParameter("id", userDto.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), equalTo(userDtoResult.getEmail()));
        assertThat(user.getName(), equalTo(userDtoResult.getName()));
    }

    @Test
    void updateUserNameNull() {
        // given
        UserDto userDto = makeUserDto("user1@mail.com", "user1");
        userDto = userService.newUser(userDto);

        UserDto userUpdate = makeUserDto("user2@mail.com", null);

        //when
        userService.updateUser(userUpdate, userDto.getId());

        // then
        TypedQuery<User> query = em.createQuery("SELECT u from User u WHERE  u.id= :id", User.class);
        User user = query.setParameter("id", userDto.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), equalTo(userUpdate.getEmail()));
        assertThat(user.getName(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
    }

    @Test
    void updateUserEmailNull() {
        // given
        UserDto userDto = makeUserDto("user1@mail.com", "user1");
        userDto = userService.newUser(userDto);

        UserDto userUpdate = makeUserDto(null, "user2");

        //when
        userService.updateUser(userUpdate, userDto.getId());

        // then
        TypedQuery<User> query = em.createQuery("SELECT u from User u WHERE  u.id= :id", User.class);
        User user = query.setParameter("id", userDto.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), notNullValue());
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getName(), equalTo(userUpdate.getName()));
    }

    @Test
    void getUserId() {
        // given
        UserDto userDto = makeUserDto("user1@mail.com", "user1");

        userDto = userService.newUser(userDto);

        em.flush();

        // when
        UserDto userTest = userService.getByUserId(userDto.getId());

        // then
        assertThat(userTest.getId(), notNullValue());
        assertThat(userTest.getEmail(), equalTo(userDto.getEmail()));
        assertThat(userTest.getName(), equalTo(userDto.getName()));
    }

    @Test
    void getUserAll() {
        // given
        List<UserDto> userDtoList = List.of(
                makeUserDto("user1@mail.com", "user1"),
                makeUserDto("user2@mail.com", "user2"),
                makeUserDto("user3@mail.com", "user3")
        );

        for (UserDto user : userDtoList) {
            User entity = UserMapper.toEntity(user);
            em.persist(entity);
        }
        em.flush();

        // when
        List<UserDto> users = userService.getUserAll();

        // then
        assertThat(users, hasSize(userDtoList.size()));
        for (UserDto user : userDtoList) {
            assertThat(users, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("email", equalTo(user.getEmail())),
                    hasProperty("name", equalTo(user.getName()))
            )));
        }
    }

    @Test
    public void deleteUser() {
        // given
        List<UserDto> userDtoList = List.of(
                makeUserDto("user1@mail.com", "user1"),
                makeUserDto("user2@mail.com", "user2"),
                makeUserDto("user3@mail.com", "user3")
        );

        for (UserDto user : userDtoList) {
            User entity = UserMapper.toEntity(user);
            em.persist(entity);
        }
        em.flush();

        TypedQuery<User> query = em.createQuery("SELECT u from User u WHERE  u.email= :email", User.class);
        User user = query.setParameter("email", "user2@mail.com")
                .getSingleResult();


        // when
        userService.deleteUser(user.getId());

        // then
        TypedQuery<User> query2 = em.createQuery("SELECT u from User u WHERE  u.email= :email", User.class);
        assertNull(null, query.toString());
    }

    private UserDto makeUserDto(String email, String name) {
        return new UserDto(null, email, name);
    }
}