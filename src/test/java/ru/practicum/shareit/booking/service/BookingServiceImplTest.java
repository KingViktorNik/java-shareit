package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInsertDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class BookingServiceImplTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private User user1;
    private User user2;
    private Item item1Owner1;
    private Item item1Owner2;
    private Booking booking1Item1Owner2User1;
    private Booking booking1Item1Owner1User2;
    private final LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    @BeforeEach
    void setUp() {
        // Пользователь 1
        UserDto userDto = userService.newUser(new UserDto(null, "user1@email.com", "user1"));
        TypedQuery<User> queryUser = em.createQuery("SELECT u FROM User u WHERE u.id=:id", User.class);
        user1 = queryUser.setParameter("id", userDto.getId())
                .getSingleResult();

        // Вещь 1 Пользователя 1
        ItemDto itemDto = itemService.addItem(user1.getId(), new ItemDto(null, "item1User1", "item1User1", true));
        TypedQuery<Item> queryItem = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        item1Owner1 = queryItem.setParameter("id", itemDto.getId())
                .getSingleResult();

        // Пользователь 2
        UserDto userDto2 = userService.newUser(new UserDto(null, "user2@email.com", "user2"));
        TypedQuery<User> queryUser2 = em.createQuery("SELECT u FROM User u WHERE u.id=:id", User.class);
        user2 = queryUser2.setParameter("id", userDto2.getId())
                .getSingleResult();

        // Вещь 1 Пользователя 2
        ItemDto itemDto2 = itemService.addItem(user2.getId(), new ItemDto(null, "item1User2", "item1User2", true));
        TypedQuery<Item> queryItem2 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        item1Owner2 = queryItem2.setParameter("id", itemDto2.getId())
                .getSingleResult();

        // Пользователем 1, Аренда 1 вещи 1 у пользователя 2
        BookingDto bookingDto2 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item1Owner2.getId(), dateTime.plusMinutes(1), dateTime.plusMinutes(2))
        );
        TypedQuery<Booking> queryBooking2 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        booking1Item1Owner2User1 = queryBooking2.setParameter("id", bookingDto2.getId())
                .getSingleResult();

        // Пользователем 2, Аренда 1 вещи 1 у пользователя 1
        BookingDto bookingDto = bookingService.addBooking(user2.getId(),
                new BookingInsertDto(item1Owner1.getId(), dateTime.plusMinutes(1), dateTime.plusMinutes(2))
        );
        TypedQuery<Booking> queryBooking = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        booking1Item1Owner1User2 = queryBooking.setParameter("id", bookingDto.getId())
                .getSingleResult();
    }

    @AfterEach
    void setClear() {
        em.createQuery("DELETE FROM User ");
        em.createQuery("DELETE FROM Item ");
        em.createQuery("DELETE FROM Booking ");
    }

    @Test
    void addBooking() {
        // given
        BookingInsertDto bookingInsertDto = new BookingInsertDto(item1Owner1.getId(), dateTime.plusMinutes(3), dateTime.plusMinutes(4));

        // when
        BookingDto bookingItem1Owner1User2 = bookingService.addBooking(user2.getId(), bookingInsertDto);

        // then
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b where b.id=:id", Booking.class);
        Booking bookingTest = query.setParameter("id", bookingItem1Owner1User2.getId())
                .getSingleResult();
        assertThat(bookingItem1Owner1User2.getId(), notNullValue());
        assertThat(bookingItem1Owner1User2.getId(), equalTo(bookingTest.getId()));
        assertThat(bookingItem1Owner1User2.getItem().getId(), equalTo(item1Owner1.getId()));
    }

    @Test
    void statusBookingOwnerStatusApproved() {
        // when
        BookingDto bookingDtoTest = bookingService.statusBooking(user2.getId(), booking1Item1Owner2User1.getId(), true);

        // then
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b where b.id=:id", Booking.class);
        Booking bookingTest = query.setParameter("id", bookingDtoTest.getId())
                .getSingleResult();
        assertThat(bookingDtoTest.getId(), notNullValue());
        assertThat(bookingDtoTest.getBooker().getId(), equalTo(user1.getId()));
        assertThat(bookingDtoTest.getStatus(), equalTo(StatusBooking.APPROVED.toString()));
    }

    @Test
    void statusBookingOwnerStatusRejected() {
        // when
        BookingDto bookingDtoTest = bookingService.statusBooking(user2.getId(), booking1Item1Owner2User1.getId(), false);

        // then
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b where b.id=:id", Booking.class);
        Booking bookingTest = query.setParameter("id", bookingDtoTest.getId())
                .getSingleResult();
        assertThat(bookingDtoTest.getId(), notNullValue());
        assertThat(bookingDtoTest.getBooker().getId(), equalTo(user1.getId()));
        assertThat(bookingDtoTest.getStatus(), equalTo(StatusBooking.REJECTED.toString()));
    }

    @Test
    void statusBookingBookerStatusCanceled() {
        // when
        BookingDto bookingDtoTest = bookingService.statusBooking(user1.getId(), booking1Item1Owner2User1.getId(), false);

        // then
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b where b.id=:id", Booking.class);
        Booking bookingTest = query.setParameter("id", bookingDtoTest.getId())
                .getSingleResult();
        assertThat(bookingDtoTest.getId(), notNullValue());
        assertThat(bookingDtoTest.getBooker().getId(), equalTo(user1.getId()));
        assertThat(bookingDtoTest.getStatus(), equalTo(StatusBooking.CANCELED.toString()));
    }

    @Test
    void getByBooking() {
        // when
        BookingDto bookingDtoTest = bookingService.getByBooking(user1.getId(), booking1Item1Owner1User2.getId());

        // then
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b where b.id=:id", Booking.class);
        Booking bookingTest = query.setParameter("id", bookingDtoTest.getId())
                .getSingleResult();
        assertThat(bookingDtoTest.getId(), notNullValue());
        assertThat(bookingDtoTest.getStatus(), equalTo(bookingTest.getStatus().toString()));
        assertThat(bookingDtoTest.getBooker().getId(), equalTo(bookingTest.getBooker().getId()));
    }

    @Test
    void getAllByUserOwnerALL() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.plusMinutes(1), dateTime.plusMinutes(2))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllByUserOwner(user2.getId(), StatusBooking.ALL.toString(), 0, 10);

        // then
        List<Booking> bookings = List.of(booking1Item1Owner2User1, booking1Item2Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllByUserOwnerCurrent() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.minusMinutes(1), dateTime.plusMinutes(1))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllByUserOwner(user2.getId(), StatusBooking.CURRENT.toString(), 0, 10);

        // then
        List<Booking> bookings = List.of(booking1Item2Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllByUserOwnerPast() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.minusMinutes(2), dateTime.minusMinutes(1))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllByUserOwner(user2.getId(), StatusBooking.PAST.toString(), 0, 10);

        // then
        List<Booking> bookings = List.of(booking1Item2Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllByUserOwnerFuture() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда(текущее) 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.minusMinutes(2), dateTime.plusMinutes(1))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда(будущее) 3 вещи 2 у пользователя 2
        BookingDto bookingDto4 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.plusMinutes(3), dateTime.plusMinutes(4))
        );
        TypedQuery<Booking> queryBooking4 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking2Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto4.getId())
                .getSingleResult();

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllByUserOwner(user2.getId(), StatusBooking.FUTURE.toString(), 0, 10);

        // then
        List<Booking> bookings = List.of(booking2Item2Owner2User1, booking1Item1Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllByUserOwnerWaitingPageSize1() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда(текущее) 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.minusMinutes(2), dateTime.plusMinutes(1))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда(будущее) 3 вещи 2 у пользователя 2
        BookingDto bookingDto4 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.plusMinutes(3), dateTime.plusMinutes(4))
        );
        TypedQuery<Booking> queryBooking4 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking2Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto4.getId())
                .getSingleResult();

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllByUserOwner(user2.getId(), StatusBooking.WAITING.toString(), 0, 1);

        // then
        List<Booking> bookings = List.of(booking2Item2Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllByUserOwnerRejected() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда(текущее) 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.minusMinutes(2), dateTime.plusMinutes(1))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        bookingService.statusBooking(user2.getId(), booking1Item2Owner2User1.getId(), false);

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllByUserOwner(user2.getId(), StatusBooking.REJECTED.toString(), 0, 10);

        // then
        List<Booking> bookings = List.of(booking1Item2Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllBookingsByUserAll() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.plusMinutes(1), dateTime.plusMinutes(2))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllBookingsByUser(user1.getId(), StatusBooking.ALL.toString(), 5, 1);

        // then
        List<Booking> bookings = List.of(booking1Item1Owner2User1);
        assertThat(bookings, hasSize(1));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllBookingsByUserCurrent() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.minusMinutes(1), dateTime.plusMinutes(1))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllBookingsByUser(user1.getId(), StatusBooking.CURRENT.toString(), 0, 10);

        // then
        List<Booking> bookings = List.of(booking1Item2Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllBookingsByUserPast() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.minusMinutes(2), dateTime.minusMinutes(1))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllBookingsByUser(user1.getId(), StatusBooking.PAST.toString(), 0, 10);

        // then
        List<Booking> bookings = List.of(booking1Item2Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllBookingsByUserFuture() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда(текущее) 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.minusMinutes(2), dateTime.plusMinutes(1))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда(будущее) 3 вещи 2 у пользователя 2
        BookingDto bookingDto4 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.plusMinutes(3), dateTime.plusMinutes(4))
        );
        TypedQuery<Booking> queryBooking4 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking2Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto4.getId())
                .getSingleResult();

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllBookingsByUser(user1.getId(), StatusBooking.FUTURE.toString(), 0, 10);

        // then
        List<Booking> bookings = List.of(booking2Item2Owner2User1, booking1Item1Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllBookingsByUserWaitingPageSize1() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда(текущее) 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.minusMinutes(2), dateTime.plusMinutes(1))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда(будущее) 3 вещи 2 у пользователя 2
        BookingDto bookingDto4 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.plusMinutes(3), dateTime.plusMinutes(4))
        );
        TypedQuery<Booking> queryBooking4 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking2Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto4.getId())
                .getSingleResult();

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllBookingsByUser(user1.getId(), StatusBooking.WAITING.toString(), 0, 1);

        // then
        List<Booking> bookings = List.of(booking2Item2Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }

    @Test
    void getAllBookingsByUserRejected() {
        // given
        // Вещь 2 Пользователя 2
        ItemDto itemDto3 = itemService.addItem(user2.getId(), new ItemDto(null, "item2User2", "item2User2", true));
        TypedQuery<Item> queryItem3 = em.createQuery("SELECT i FROM Item i WHERE i.id=:id", Item.class);
        Item item2Owner2 = queryItem3.setParameter("id", itemDto3.getId())
                .getSingleResult();

        // Пользователем 1, Аренда(текущее) 2 вещи 2 у пользователя 2
        BookingDto bookingDto3 = bookingService.addBooking(user1.getId(),
                new BookingInsertDto(item2Owner2.getId(), dateTime.minusMinutes(2), dateTime.plusMinutes(1))
        );
        TypedQuery<Booking> queryBooking3 = em.createQuery("SELECT b FROM Booking b WHERE b.id=:id", Booking.class);
        Booking booking1Item2Owner2User1 = queryBooking3.setParameter("id", bookingDto3.getId())
                .getSingleResult();

        bookingService.statusBooking(user2.getId(), booking1Item2Owner2User1.getId(), false);

        // when
        List<BookingDto> allByUserOwner = bookingService.getAllBookingsByUser(user1.getId(), StatusBooking.REJECTED.toString(), 0, 10);

        // then
        List<Booking> bookings = List.of(booking1Item2Owner2User1);
        assertThat(bookings, hasSize(allByUserOwner.size()));
        for (BookingDto booking : allByUserOwner) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("id", equalTo(booking.getId()))
            )));
        }
    }
}