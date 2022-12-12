package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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
class ItemRequestServiceTest {
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private LocalDateTime dateTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@email.com");
        itemRequest = new ItemRequest(1L, "description", dateTime, user);
        itemRequestDto = new ItemRequestDto(1L, "description", dateTime, List.of());
    }

    @Test
    void addItemRequestNullUserId() {
        // when
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemRequestService.addItemRequest(null, null)
        );

        // then
        Assertions.assertEquals("missing header data 'X-Sharer-User-Id'", exception.getMessage());
    }

    @Test
    void addItemRequestNotFound() {
        // when
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> itemRequestService.addItemRequest(555L, null)
        );

        // then
        Assertions.assertEquals("User with id: " + 555L + " not found", exception.getMessage());
    }

    @Test
    void addItemRequestHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        // when
        itemRequestService.addItemRequest(anyLong(), itemRequestDto);

        // then
        verify(userRepository, times(1)).findById(any());
        verify(requestRepository, times(1)).save(any());
        verifyNoMoreInteractions(userRepository, requestRepository);
    }

    @Test
    void getAllRequestsByUserNullUserId() {
        // when
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAllRequestsByUser(null)
        );

        // then
        Assertions.assertEquals("missing header data 'X-Sharer-User-Id'", exception.getMessage());
    }

    @Test
    void getAllRequestsByUserNotFount() {
        // when
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> itemRequestService.getAllRequestsByUser(555L)
        );

        // then
        Assertions.assertEquals("User with id: " + 555L + " not found", exception.getMessage());
    }

    @Test
    void getAllRequestsByUserHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterId_Id(anyLong()))
                .thenReturn(List.of(itemRequest));
        // when
        itemRequestService.getAllRequestsByUser(user.getId());

        // then
        verify(userRepository, times(1)).findById(any());
        verify(requestRepository, times(1)).findAllByRequesterId_Id(any());
        verifyNoMoreInteractions(userRepository, requestRepository);
    }

    @Test
    void getAllRequestsNotUserId() {
        // when
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAllRequests(null, null, null)
        );

        // then
        Assertions.assertEquals("missing header data 'X-Sharer-User-Id'", exception.getMessage());
    }

    @Test
    void getAllRequestsNotFount() {
        // when
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> itemRequestService.getAllRequests(555L, null, null)
        );

        // then
        Assertions.assertEquals("User with id: " + 555L + " not found", exception.getMessage());
    }

    @Test
    void getAllRequestsHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.getAllByRequesterUsers(any(), anyLong()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        // when
        itemRequestService.getAllRequests(555L, 0, 10);

        // then
        verify(userRepository, times(1)).findById(any());
        verify(requestRepository, times(1)).getAllByRequesterUsers(any(), anyLong());
        verifyNoMoreInteractions(userRepository, requestRepository);
    }

    @Test
    void getRequestNotUserId() {
        // when
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemRequestService.getRequest(null, null)
        );

        // then
        Assertions.assertEquals("missing header data 'X-Sharer-User-Id'", exception.getMessage());
    }

    @Test
    void getRequestNotFount() {
        // when
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> itemRequestService.getRequest(555L, null)
        );

        // then
        Assertions.assertEquals("User with id: " + 555L + " not found", exception.getMessage());
    }

    @Test
    void getRequestNotFountRequest() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        // when
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> itemRequestService.getRequest(user.getId(), 555L)
        );

        // then
        Assertions.assertEquals("Request with id: 555 not found", exception.getMessage());
    }

    @Test
    void getRequestHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        // when
        itemRequestService.getRequest(user.getId(), 555L);

        // then
        verify(userRepository, times(1)).findById(any());
        verify(requestRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository, requestRepository);
    }
}