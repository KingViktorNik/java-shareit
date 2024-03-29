package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addBooking(@NotBlank Long userId, BookingInsertDto bookingInsertDto) {
        Item item = itemRepository.findById(bookingInsertDto.getItemId())
                .orElseThrow(() -> new NullObjectException("Item with id:" + bookingInsertDto.getItemId() + " not found"));

        // Является ли пользователь владельцем вещи
        if (item.getOwnerId().equals(userId)) {
            throw new NullObjectException("This thing is yours");
        }

        // Проверка статуса вещи
        if (!item.getAvailable()) {
            throw new ValidationException("Item with id:" + item.getName() + " Unavailable");
        }


        // Проверка даты аренды
        List<Booking> bookingTest = new ArrayList<>(bookingRepository.getBookingByDate(item.getId(), bookingInsertDto.getStart(),
                bookingInsertDto.getEnd()));
        if (bookingTest.size() != 0) {
            throw new ValidationException("The current rental period is busy");
        }


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));


        Booking booking = bookingMapper.toEntity(bookingInsertDto);

        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(StatusBooking.WAITING);

        booking = bookingRepository.save(booking);

        log.info("add booking - id:{} name:{}", item.getId(), item.getName());

        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto statusBooking(Long userId, Long bookerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookerId)
                .orElseThrow(() -> new NullObjectException("Booking with id:" + bookerId + " not found"));

        if (approved) {
            if (booking.getStatus().equals(StatusBooking.APPROVED)) {
                throw new ValidationException("Cannot be changed");
            }
            if (booking.getItem().getOwnerId().equals(userId)) {
                booking.setStatus(StatusBooking.APPROVED);
            } else {
                throw new NullObjectException("Cannot be changed");
            }
        } else {
            if (booking.getItem().getOwnerId().equals(userId)) {
                booking.setStatus(StatusBooking.REJECTED);
            } else {
                booking.setStatus(StatusBooking.CANCELED);
            }
        }

        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getByBooking(Long userId, Long bookerId) {
        Booking booking = bookingRepository.getByBooker(userId,
                bookerId).orElseThrow(() -> new NullObjectException("booking not found"));
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUserOwner(Long userId, StatusBooking state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));

        switch (state) {
            case ALL: // все
                return bookingRepository.findAllByItem_OwnerId(
                                PageRequest.of(from, size,
                                        Sort.by(Sort.Direction.DESC, "StartDate")
                                ),
                                userId
                        ).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case CURRENT: // текущие
                return bookingRepository.getBookingOwnerCurrent(
                                PageRequest.of(from, size),
                                userId,
                                LocalDateTime.now()
                        ).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case PAST: // завершенные
                return bookingRepository.findAllByItem_OwnerIdAndEndDateBefore(
                                PageRequest.of(from, size,
                                        Sort.by(Sort.Direction.DESC, "StartDate")
                                ),
                                userId,
                                LocalDateTime.now()
                        ).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case FUTURE: // будущие
                return bookingRepository.findAllByItem_OwnerIdAndStartDateAfter(
                                PageRequest.of(from, size,
                                        Sort.by(Sort.Direction.DESC, "StartDate")
                                ),
                                userId,
                                LocalDateTime.now()
                        ).stream()
                        .sorted((o1, o2) -> (o2.getStartDate().compareTo(o1.getStartDate())))
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case WAITING: // ожидающие подтверждения
                return bookingRepository.getBookingWaitingByOwner(
                                PageRequest.of(from, size),
                                userId
                        ).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case REJECTED: // отклонённые
                return bookingRepository.findAllByStatusAndItem_OwnerId(
                                PageRequest.of(from, size,
                                        Sort.by(Sort.Direction.DESC, "StartDate")
                                ),
                                StatusBooking.REJECTED,
                                userId
                        ).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            default:
                throw new NullObjectException("Unknown state: " + state.name());
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByUser(Long userId, StatusBooking state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));

        switch (state) {
            case ALL: // все
                Page<Booking> bookingPage = bookingRepository.findAllByBooker_Id(
                        PageRequest.of(from, size,
                                Sort.by(Sort.Direction.DESC, "StartDate")
                        ),
                        userId
                );

                if ((bookingPage.getTotalElements() % bookingPage.getTotalPages()) < from) {
                    from = Math.toIntExact(bookingPage.getTotalElements() % bookingPage.getTotalPages());
                    bookingPage = bookingRepository.findAllByBooker_Id(
                            PageRequest.of(from, size,
                                    Sort.by(Sort.Direction.DESC, "StartDate")
                            ),
                            userId
                    );
                }

                return bookingPage.stream()
                        .sorted((o1, o2) -> (o2.getStartDate().compareTo(o1.getStartDate())))
                        .map(bookingMapper::toDto)
                        .collect(toList());

            case CURRENT: // текущие
                return bookingRepository.getBookingCurrent(
                                PageRequest.of(from, size),
                                userId,
                                LocalDateTime.now()
                        ).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());

            case PAST: // завершенные
                return bookingRepository.findAllByBooker_IdAndEndDateBefore(
                                PageRequest.of(
                                        from,
                                        size,
                                        Sort.by(Sort.Direction.DESC, "StartDate")
                                ),
                                userId,
                                LocalDateTime.now()
                        ).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case FUTURE: // будущие
                return bookingRepository.findAllByBooker_IdAndStartDateIsAfter(
                                PageRequest.of(
                                        from,
                                        size,
                                        Sort.by(Sort.Direction.DESC, "StartDate")
                                ),
                                userId,
                                LocalDateTime.now()
                        ).stream()
                        .sorted((o1, o2) -> (o2.getStartDate().compareTo(o1.getStartDate())))
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case WAITING: // ожидающие подтверждения
                return bookingRepository.getBookingStatusByUser(
                                PageRequest.of(
                                        from,
                                        size
                                ),
                                userId,
                                state
                        ).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case REJECTED: // отклонённые
                return bookingRepository.getBookingRejected(
                                PageRequest.of(
                                        from,
                                        size
                                ),
                                userId
                        ).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            default:
                throw new NullObjectException("Unknown state: " + state.name());
        }
    }
}
