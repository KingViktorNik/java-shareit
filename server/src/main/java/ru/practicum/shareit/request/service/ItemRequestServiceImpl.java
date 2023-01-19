package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));

        ItemRequest newRequest = ItemRequestMapper.toEntity(itemRequestDto);
        newRequest.setRequester(user);
        newRequest.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        newRequest = requestRepository.save(newRequest);

        log.info("new request - id:'{}' name:'{}'", newRequest.getId(), newRequest.getDescription());
        return ItemRequestMapper.toDto(newRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));

        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterId_Id(userId);

        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
                    List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
                    itemRequestDto.setItems(ItemRequestMapper.toDtoListItem(items));
                    return itemRequestDto;
                })
                .collect(toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));

        Page<ItemRequest> itemRequests = requestRepository.getAllByRequesterUsers(PageRequest.of(from, size), userId);
        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
                    List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
                    itemRequestDto.setItems(ItemRequestMapper.toDtoListItem(items));
                    return itemRequestDto;
                })
                .collect(toList());
    }

    @Override
    public ItemRequestDto getRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));

        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NullObjectException("Request with id: " + requestId + " not found"));
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
        itemRequestDto.setItems(ItemRequestMapper.toDtoListItem(items));
        return itemRequestDto;
    }
}
