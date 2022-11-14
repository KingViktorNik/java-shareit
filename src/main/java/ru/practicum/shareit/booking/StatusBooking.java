package ru.practicum.shareit.booking;

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
    CANCELED
}
