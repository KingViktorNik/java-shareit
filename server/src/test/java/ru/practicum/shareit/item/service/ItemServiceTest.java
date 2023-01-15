package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@email.com");
        item = new Item(1L, "item1", "itemDescription1", true, user.getId(), null);
        itemDto = makeItemDto("item1", "itemDescription1");
    }

    @Test
    void newUserItem() {
        //given
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);

        //when
        ItemDto itemTest = itemService.addItem(user.getId(), itemDto);

        //then
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).save(any());
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void newItemInvalidUserId() {
        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> itemService.addItem(1L, itemDto)
        );

        // then
        Assertions.assertEquals("User with id: 1 not found", exception.getMessage());
    }

    @Test
    void updateItem() {
        //given
        Item itemUpdate = new Item(item.getId(), "itemUpdate", "itemUpdateDescription",
                false, user.getId(), null
        );
        ItemDto itemDtoUpdate = makeItemDto("itemUpdate", "itemUpdateDescription");

        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(itemUpdate);

        //when
        ItemDto itemTest = itemService.updateItem(user.getId(), itemDtoUpdate);

        //then
        verify(itemRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).save(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void updateItemNullData() {
        //given
        Item itemUpdate = new Item(item.getId(), "itemUpdate", "itemUpdateDescription",
                false, user.getId(), null
        );
        ItemDto itemDtoUpdate = makeItemDto(null, null);
        itemDtoUpdate.setAvailable(null);

        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(itemUpdate);

        //when
        ItemDto itemTest = itemService.updateItem(user.getId(), itemDtoUpdate);

        //then
        assertThat(itemTest.getId(), notNullValue());
        verify(itemRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).save(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void updateItemInvalidItemId() {
        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> itemService.updateItem(1L, itemDto)
        );

        // then
        Assertions.assertEquals("Item with id:null not found", exception.getMessage());
    }

    @Test
    void updateItemIdOwnerIdNotEquals() {
        // given
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> itemService.updateItem(555L, itemDto)
        );

        // then
        Assertions.assertEquals("The user with id:555 does not have such an item", exception.getMessage());
    }

    @Test
    void getByItemIdOwnerIdNot() {
        // given
        when(commentRepository.findAllByItem_Id(item.getId()))
                .thenReturn(null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        // when
        ItemDto itemTest = itemService.getByItemId(555L, item.getId());

        // then
        assertThat(itemTest.getId(), notNullValue());
        verify(commentRepository, times(1)).findAllByItem_Id(any());
        verify(itemRepository, times(1)).findById(any());
        verifyNoMoreInteractions(commentRepository, itemRepository);
    }

    @Test
    void getByItemIdOwnerId() {
        // given
        when(commentRepository.findAllByItem_Id(item.getId()))
                .thenReturn(null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        // when
        ItemDto itemTest = itemService.getByItemId(user.getId(), item.getId());

        // then
        assertThat(itemTest.getId(), notNullValue());
        verify(commentRepository, times(1)).findAllByItem_Id(any());
        verify(itemRepository, times(1)).findById(any());
        verifyNoMoreInteractions(commentRepository, itemRepository);
    }

    @Test
    void itemAllByUserId() {
        //given
        List<Item> items = List.of(item);
        when(itemRepository.findAllByOwnerId(user.getId()))
                .thenReturn(items);

        // when
        List<ItemDto> itemsTest = itemService.getByUserIdItemAll(user.getId());

        // then
        assertThat(itemsTest, hasSize(1));
        verify(itemRepository, times(1)).findAllByOwnerId(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void itemAllByUserIdListBookings() {
        //given
        Item item1 = new Item(1L, "item1", "item1", true, 1L, null);
        Item item2 = new Item(3L, "item2", "item2", true, 1L, null);
        Item item3 = new Item(2L, "item3", "item3", true, 1L, null);

        List<Item> items = List.of(item1, item2, item3);

        List<Booking> bookings = List.of(
                new Booking(1L, LocalDateTime.now().plusMinutes(3),LocalDateTime.now().plusMinutes(4),
                        item1, user, StatusBooking.WAITING),
                new Booking(2L, null, null,
                        item2, user, StatusBooking.WAITING),
                new Booking(3L, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(6),
                        item3, user, StatusBooking.WAITING)
        );

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(items);
        when(bookingRepository.getLastBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookings);
        when(bookingRepository.getNextBooking(anyLong()))
                .thenReturn(Optional.of(bookings.get(2)));

        // when
        List<ItemDto> itemsTest = itemService.getByUserIdItemAll(user.getId());

        // then
        assertThat(itemsTest, hasSize(3));
        verify(itemRepository, times(1)).findAllByOwnerId(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getItemSearchListIsEmpty() {
        //given
        String search = "";

        // when
        List<ItemDto> itemDtoList = itemService.getItemSearch(search);

        // then
        assertTrue(itemDtoList.isEmpty());
    }

    @Test
    void getItemSearch() {
        //given
        String search = "item";

        when(itemRepository.getItemByName("%" + search + "%"))
                .thenReturn(List.of(item));
        // when
        List<ItemDto> itemDtoList = itemService.getItemSearch(search);

        // then
        assertFalse(itemDtoList.isEmpty());
        assertThat(itemDtoList, hasSize(1));
        verify(itemRepository, times(1)).getItemByName(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void addCommentUserIdBookingNull() {
        //given
        when(bookingRepository.commentByBookerId(anyLong(), anyLong(), any()))
                .thenReturn(List.of());

        // when
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(1L, 1L, new CommentDto(null,"text", null, null))
        );

        // then
        Assertions.assertEquals("User with id:'1' did not rent a thing with id:'1'", exception.getMessage());
    }

    @Test
    void addComment() {
        //given
        when(bookingRepository.commentByBookerId(anyLong(), anyLong(), any()))
                .thenReturn(List.of(new Booking(2L,
                        LocalDateTime.now().minusHours(1),
                        LocalDateTime.now().plusHours(1),
                        new Item(1L, "panda toy", "panda toy", true, 2L, null),
                        new User(1L, "userOne", "userOne@mail.com"),
                        StatusBooking.APPROVED)));
        when(commentRepository.save(any()))
                .thenReturn(new Comment(1L, "text", item, user, Instant.now()));

        // when
        CommentDto commentDto = itemService.addComment(1L, 1L, new CommentDto(null, "text", null, null));

        // then
        assertThat(commentDto.getId(), notNullValue());
        verify(bookingRepository, times(1)).commentByBookerId(anyLong(), anyLong(), any());
        verify(commentRepository, times(1)).save(any());
        verifyNoMoreInteractions(bookingRepository, commentRepository);

    }

    private ItemDto makeItemDto(String name, String description) {
        return new ItemDto(null,
                name,
                description,
                true,
                null,
                null,
                null,
                null
        );
    }
}