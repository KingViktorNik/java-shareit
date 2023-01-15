package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInsertDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingInsertDto bookingInsertDto;
    private BookingDto bookingDtoResult;
    private LocalDateTime dateTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@mail.com");
        owner = new User(2L, "Owner", "owner@mail.com");
        item = new Item(2L, "item", "description", true, 2L, null);
        bookingInsertDto = new BookingInsertDto(2L, dateTime.plusHours(1), dateTime.plusHours(2));
        booking = new Booking(2L,
                dateTime.plusHours(1),
                dateTime.plusHours(2),
                item,
                user,
                StatusBooking.APPROVED
        );
        bookingDtoResult = new BookingDto(2L,
                dateTime.plusHours(1), dateTime.plusHours(2), StatusBooking.APPROVED.toString());
        bookingDtoResult.getBooker().setId(booking.getBooker().getId());
        bookingDtoResult.getItem().setId(booking.getItem().getId());
        bookingDtoResult.getItem().setName(booking.getItem().getName());
    }

    @Test
    void addBookingNotListToBooking() {
        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> bookingService.addBooking(user.getId(), bookingInsertDto)
        );

        //then
        Assertions.assertEquals("Item with id:" + bookingInsertDto.getItemId() + " not found", exception.getMessage());
    }

    @Test
    void addBookingIsNotTheOwnerOfTheItem() {
        // given
        item.setId(2L); //не верный id пользователя
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> bookingService.addBooking(owner.getId(), bookingInsertDto)
        );

        // then
        Assertions.assertEquals("This thing is yours", exception.getMessage());
    }

    @Test
    void addBookingItemIsNotAvailable() {
        // given
        item.setAvailable(false); // вещь не доступна
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        // when
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(user.getId(), bookingInsertDto)
        );

        // then
        Assertions.assertEquals("Item with id:" + item.getName() + " Unavailable", exception.getMessage());
    }

    @Test
    void addBookingItemIsAlreadyRented() {
        // given
        List<Booking> booking = List.of(new Booking(2L,
                                                        dateTime.plusMinutes(30),
                                                        dateTime.plusMinutes(90),
                                                        item,
                                                        user,
                                                        StatusBooking.APPROVED)
        );

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.getBookingByDate(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(booking);

        // when
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(user.getId(), bookingInsertDto)
        );

        // then
        Assertions.assertEquals("The current rental period is busy", exception.getMessage());
    }

    @Test
    void addBookingUserNotFound() {
        // given

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.getBookingByDate(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> bookingService.addBooking(user.getId(), bookingInsertDto)
        );

        // then
        Assertions.assertEquals("User with id: " + user.getId() + " not found", exception.getMessage());
    }

    @Test
    void addBookingHappy() {
        // given

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.getBookingByDate(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingMapper.toEntity(bookingInsertDto))
                .thenReturn(booking);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        when(bookingMapper.toDto(any(Booking.class)))
                .thenReturn(bookingDtoResult);

        // when
        bookingService.addBooking(user.getId(), bookingInsertDto);

        // then
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingByDate(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingMapper, times(1)).toEntity(any(BookingInsertDto.class));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, times(1)).toDto(any(Booking.class));
        verifyNoMoreInteractions(itemRepository, bookingRepository, userRepository, bookingMapper);
    }

    @Test
    void statusBookingNotFount() {
        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> bookingService.statusBooking(null, 555L, true)
        );

        // then
        Assertions.assertEquals("Booking with id:" + 555L + " not found", exception.getMessage());
    }

    @Test
    void statusBookingCannotBeChanged() {
        // given
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        // when
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.statusBooking(null, booking.getId(), true)
        );

        // then
        Assertions.assertEquals("Cannot be changed", exception.getMessage());
    }

    @Test
    void statusBookingNotUserItem() {
        // given
        booking.setStatus(StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> bookingService.statusBooking(null, booking.getId(), true)
        );

        // then
        Assertions.assertEquals("Cannot be changed", exception.getMessage());
    }

    @Test
    void statusBookingOwnerHappy() {
        // given
        booking.setStatus(StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        when(bookingMapper.toDto(any(Booking.class)))
                .thenReturn(bookingDtoResult);

        // when
        bookingService.statusBooking(booking.getItem().getOwnerId(), booking.getId(), true);

        // then
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, times(1)).toDto(any(Booking.class));
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void statusBookingBookerHappyRejected() {
        // given
        booking.setStatus(StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        when(bookingMapper.toDto(any(Booking.class)))
                .thenReturn(bookingDtoResult);

        // when
        bookingService.statusBooking(booking.getItem().getOwnerId(), booking.getId(), false);

        // then
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, times(1)).toDto(any(Booking.class));
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void statusBookingBookerHappyCanceled() {
        // given
        booking.setStatus(StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        when(bookingMapper.toDto(any(Booking.class)))
                .thenReturn(bookingDtoResult);

        // when
        bookingService.statusBooking(user.getId(), booking.getId(), false);

        // then
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, times(1)).toDto(any(Booking.class));
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void getByBookingNotFound() {
        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> bookingService.getByBooking(null, null)
        );

        // then
        Assertions.assertEquals("booking not found", exception.getMessage());
    }

    @Test
    void getByBooking() {
        // given
        when(bookingRepository.getByBooker(anyLong(),anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any(Booking.class)))
                .thenReturn(bookingDtoResult);

        // when
        bookingService.getByBooking(anyLong(), anyLong());
        // then
        verify(bookingRepository, times(1)).getByBooker(anyLong(), anyLong());
        verify(bookingMapper, times(1)).toDto(any(Booking.class));
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void getAllByUserOwnerNotFound() {
        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> bookingService.getAllByUserOwner(555L, null, 0, 10)
        );

        // then
        Assertions.assertEquals("User with id: " + 555L + " not found", exception.getMessage());
    }


    @Test
    void getAllByUserOwnerAllHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItem_OwnerId(any(PageRequest.class),anyLong()))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllByUserOwner(user.getId(), StatusBooking.ALL, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItem_OwnerId(any(PageRequest.class), anyLong());
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllByUserOwnerCurrentHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.getBookingOwnerCurrent(any(PageRequest.class),anyLong(), any(LocalDateTime.class)))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllByUserOwner(user.getId(), StatusBooking.CURRENT, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingOwnerCurrent(any(PageRequest.class), anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllByUserOwnerPastHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItem_OwnerIdAndEndDateBefore(any(PageRequest.class),anyLong(), any(LocalDateTime.class)))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllByUserOwner(user.getId(), StatusBooking.PAST, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndEndDateBefore(any(PageRequest.class), anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllByUserOwnerFutureHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItem_OwnerIdAndStartDateAfter(any(PageRequest.class),anyLong(), any(LocalDateTime.class)))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllByUserOwner(user.getId(), StatusBooking.FUTURE, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndStartDateAfter(any(PageRequest.class), anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllByUserOwnerWaitingHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.getBookingWaitingByOwner(any(PageRequest.class),anyLong()))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllByUserOwner(user.getId(), StatusBooking.WAITING, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingWaitingByOwner(any(PageRequest.class), anyLong());
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllByUserOwnerRejectedHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllByStatusAndItem_OwnerId(any(PageRequest.class), any(StatusBooking.class), anyLong()))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllByUserOwner(user.getId(), StatusBooking.REJECTED, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByStatusAndItem_OwnerId(any(PageRequest.class), any(StatusBooking.class), anyLong());
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllBookingsByUserNotFound() {
        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> bookingService.getAllBookingsByUser(555L, null, 0, 10)
        );

        // then
        Assertions.assertEquals("User with id: " + 555L + " not found", exception.getMessage());
    }

    @Test
    void getAllBookingsByUserAllHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBooker_Id(any(PageRequest.class),anyLong()))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllBookingsByUser(user.getId(), StatusBooking.ALL, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBooker_Id(any(PageRequest.class), anyLong());
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllBookingsByUserCurrentHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.getBookingCurrent(any(PageRequest.class),anyLong(), any(LocalDateTime.class)))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllBookingsByUser(user.getId(), StatusBooking.CURRENT, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingCurrent(any(PageRequest.class), anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllBookingsByUserPastHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBooker_IdAndEndDateBefore(any(PageRequest.class),anyLong(), any(LocalDateTime.class)))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllBookingsByUser(user.getId(), StatusBooking.PAST, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBooker_IdAndEndDateBefore(any(PageRequest.class), anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllBookingsByUserFutureHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBooker_IdAndStartDateIsAfter(any(PageRequest.class),anyLong(), any(LocalDateTime.class)))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllBookingsByUser(user.getId(), StatusBooking.FUTURE, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBooker_IdAndStartDateIsAfter(any(PageRequest.class), anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllBookingsByUserWaitingHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.getBookingStatusByUser(any(PageRequest.class),anyLong(), any(StatusBooking.class)))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllBookingsByUser(user.getId(), StatusBooking.WAITING, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingStatusByUser(any(PageRequest.class), anyLong(), any(StatusBooking.class));
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllBookingsByUserRejectedHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.getBookingRejected(any(PageRequest.class), anyLong()))
                .thenReturn(Page.empty());
        // when
        bookingService.getAllBookingsByUser(user.getId(), StatusBooking.REJECTED, 0, 10);

        // then
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingRejected(any(PageRequest.class), anyLong());
        verifyNoMoreInteractions(userRepository, bookingRepository);
    }
}

