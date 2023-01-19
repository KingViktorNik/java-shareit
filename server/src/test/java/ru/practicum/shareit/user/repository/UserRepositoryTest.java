package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest(properties = "spring.jpa.show-sql = true")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyAddUser() {
        // given
        User user = new User(null, "user1", "user1@mail.com");

        // when
        userRepository.save(user);

        // then
        assertThat(user.getId(), notNullValue());
    }

    @Test
    void verifyUpdateUser() {
        // given
        User user = em.persist(new User(null, "user2", "user2@mail.com"));
        User userNew = new User(user.getId(), "user3", "user3@mail.com");

        // when and then
        User userTest = em.find(User.class, user.getId());
        assertNotEquals(userNew.getName(), userTest.getName());
        userRepository.save(userNew);
        assertEquals(userNew.getEmail(), userTest.getEmail());
    }

    @Test
    void verifyGetByUserId() {
        // given
        User user1 = em.persist(new User(null, "user4", "user4@mail.com"));
        User user2 = em.persist(new User(null, "user5", "user5@mail.com"));

        // when
        User userTest = userRepository.findById(user2.getId()).orElse(null);

        // then
        assertThat(userTest.getId(), notNullValue());
        assertThat(userTest.getEmail(), equalTo(user2.getEmail()));
        assertThat(userTest.getName(), equalTo(user2.getName()));
    }

    @Test
    void verifyGetUserAll() {
        // given
        User user1 = em.persist(new User(null, "user6", "user6@mail.com"));
        User user2 = em.persist(new User(null, "user7", "user7@mail.com"));

        // when
        List<User> usersTest = new LinkedList<>(userRepository.findAll());

        // then
        assertThat(usersTest, hasSize(2));
        assertThat(usersTest.get(0).getId(), equalTo(user1.getId()));
        assertThat(usersTest.get(0).getEmail(), equalTo("user6@mail.com"));
        assertThat(usersTest.get(0).getName(), equalTo("user6"));
        assertThat(usersTest.get(1).getId(), equalTo(user2.getId()));
        assertThat(usersTest.get(1).getEmail(), equalTo("user7@mail.com"));
        assertThat(usersTest.get(1).getName(), equalTo("user7"));
    }
}