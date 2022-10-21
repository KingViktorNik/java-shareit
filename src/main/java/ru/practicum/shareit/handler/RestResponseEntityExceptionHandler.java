package ru.practicum.shareit.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String PATH = "path";
    private static final String REASONS = "reasons";

    @ExceptionHandler
    public ResponseEntity<?> handlerNullObjectException(final NullObjectException ex, WebRequest request) {
        logger.error("not found error. Message: {}", ex.getMessage(), ex);
        Map<String, Object> body = getGeneralErrorBody(HttpStatus.NOT_FOUND, request);
        body.put(REASONS, ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerValidationException(final ValidationException ex, WebRequest request) {
        logger.error("not valid. Message: {}", ex.getMessage(), ex);
        Map<String, Object> body = getGeneralErrorBody(HttpStatus.BAD_REQUEST, request);
        body.put(REASONS, ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerConflictException(final ConflictException ex, WebRequest request) {
        logger.error("conflict. Message: {}", ex.getMessage(), ex);
        Map<String, Object> body = getGeneralErrorBody(HttpStatus.CONFLICT, request);
        body.put(REASONS, ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleThrowable(final Throwable e) {
        return new ResponseEntity<>("an unexpected error has occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        logger.error("not valid. Message: {}", e.getMessage(), e);
        Map<String, Object> body = getGeneralErrorBody(status, request);
        List<String> errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::getErrorString).collect(Collectors.toList());
        body.put(REASONS, errors);
        return new ResponseEntity<>(body, headers, status);
    }

    private Map<String, Object> getGeneralErrorBody(HttpStatus status,
                                                    WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, OffsetDateTime.now());
        body.put(STATUS, status.value());
        body.put(ERROR, status.getReasonPhrase());
        body.put(PATH, getRequestURI(request));
        return body;
    }

    private String getErrorString(ObjectError error) {
        if (error instanceof FieldError) {
            return ((FieldError) error).getField() + ' ' + error.getDefaultMessage();
        }
        return error.getDefaultMessage();
    }

    private String getRequestURI(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            HttpServletRequest requestHttp = ((ServletWebRequest) request).getRequest();
            return String.format("%s %s", requestHttp.getMethod(), requestHttp.getRequestURI());
        } else {
            return "";
        }
    }
}