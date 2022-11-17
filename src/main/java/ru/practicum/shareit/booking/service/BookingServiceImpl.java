package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDate;
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
        if (userId == null) {
            throw new ValidationException("missing header data 'X-Sharer-User-Id'");
        }

        Item item = itemRepository.findById(bookingInsertDto.getItemId())
                .orElseThrow(() -> new NullObjectException("Item with id:" + bookingInsertDto.getItemId() + " not found"));

        // Является ли пользователь владельцем вещи
        if (item.getOwner().getId().equals(userId)) {
            throw new NullObjectException("This thing is yours");
        }

        // Проверка статуса вещи
        if (!item.getAvailable()) {
            throw new ValidationException("Item with id:" + item.getName() + " Unavailable");
        }

        // Дата аренды не должна быть в прошлом
        if (bookingInsertDto.getStart().isBefore(LocalDate.now().atStartOfDay())
                // дата окончания аренды не должна быть меньше даты аренды
                || bookingInsertDto.getEnd().isBefore(bookingInsertDto.getStart())
                // дата начала аренды не должно быть позднее даты окончания аренды
                || bookingInsertDto.getStart().isAfter(bookingInsertDto.getEnd())) {
            throw new ValidationException(bookingInsertDto.getEnd() + " It can't be in the past");
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
            if (booking.getItem().getOwner().getId().equals(userId)) {
                booking.setStatus(StatusBooking.APPROVED);
            } else {
                throw new NullObjectException("Cannot be changed");
            }
        } else {
            if (booking.getItem().getOwner().getId().equals(userId)) {
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
    public List<BookingDto> getAllByUserOwner(Long userId, String statusSt) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));

        StatusBooking status;

        try {
            status = StatusBooking.valueOf(statusSt);
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + statusSt);
        }

        switch (status) {
            case ALL: // все
                return bookingRepository.findAllByItem_Owner_Id(userId).stream()
                        .sorted((o1, o2) -> (o2.getStartDate().compareTo(o1.getStartDate())))
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case CURRENT: // текущие
                return bookingRepository.getBookingOwnerCurrent(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case PAST: // завершенные
                return bookingRepository.findAllByItem_Owner_IdAndEndDateBefore(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case FUTURE: // будущие
                return bookingRepository.findAllByItem_Owner_IdAndEndDateAfter(userId, LocalDateTime.now()).stream()
                        .sorted((o1, o2) -> (o2.getStartDate().compareTo(o1.getStartDate())))
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case WAITING: // ожидающие подтверждения
                return bookingRepository.getBookingWaitingByOwner(userId).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case REJECTED: // отклонённые
                return bookingRepository.findAllByStatusAndItem_Owner_Id(StatusBooking.REJECTED, userId).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            default:
                throw new NullObjectException("Unknown state: " + status.name());
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByUser(Long userId, String statusSt) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));

        StatusBooking status;

        try {
            status = StatusBooking.valueOf(statusSt);
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + statusSt);
        }

        switch (status) {
            case ALL: // все
                return bookingRepository.findAllByBooker_Id(userId).stream()
                        .sorted((o1, o2) -> (o2.getStartDate().compareTo(o1.getStartDate())))
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case CURRENT: // текущие
                return bookingRepository.getBookingCurrent(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case PAST: // завершенные
                return bookingRepository.findAllByBooker_IdAndEndDateBefore(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case FUTURE: // будущие
                return bookingRepository.findAllByBooker_IdAndStartDateIsAfter(userId, LocalDateTime.now()).stream()
                        .sorted((o1, o2) -> (o2.getStartDate().compareTo(o1.getStartDate())))
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case WAITING: // ожидающие подтверждения
                return bookingRepository.getBookingStatusByUser(userId, status).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            case REJECTED: // отклонённые
                return bookingRepository.getBookingRejected(userId).stream()
                        .map(bookingMapper::toDto)
                        .collect(toList());
            default:
                throw new NullObjectException("Unknown state: " + status.name());
        }
    }
}
