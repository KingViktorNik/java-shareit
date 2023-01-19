package ru.practicum.shareit.exception;

public class NullObjectException extends NullPointerException {
    public NullObjectException(String massage) {
        super(massage);
    }
}