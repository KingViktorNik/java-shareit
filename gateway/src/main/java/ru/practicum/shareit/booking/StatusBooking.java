package ru.practicum.shareit.booking;

import java.util.Optional;

public enum StatusBooking {
    /** @All - все **/
    ALL,
    /** @CURRENT - текущие **/
    CURRENT,
    /** @PAST - завершённые **/
    PAST,
    /** @FUTURE - будущие **/
    FUTURE,
    /** @WAITING - новое бронирование, ожидает одобрения **/
    WAITING,
    /** @APPROVED - бронирование подтверждено владельцем(статус ДБ) **/
    APPROVED,
    /** @REJECTED - бронирование отклонено владельцем(статус ДБ) **/
    REJECTED,
    /** @CANCELED - бронирование отменено создателем(статус ДБ) **/
    CANCELED;

    public static Optional<StatusBooking> from(String stringState) {
        for (StatusBooking state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }

}
