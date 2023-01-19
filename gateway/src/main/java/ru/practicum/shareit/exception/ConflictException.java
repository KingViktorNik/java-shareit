package ru.practicum.shareit.exception;

public class ConflictException extends NullPointerException {
    public ConflictException(String massage) {
        super(massage);
    }
}