package ru.stepagin.core.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerService {
    //TODO: добавить отлов кастомных исключений

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleException(final AccessDeniedException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.forbidden(e.getMessage());
        log.info("[{}] AccessDeniedException: {}", exceptionResponse.getId(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(final MethodArgumentNotValidException e) {
        final String message = Arrays.stream(Objects.requireNonNull(e.getDetailMessageArguments())).toList().get(1).toString();
        ExceptionResponse exceptionResponse = ExceptionResponse.badRequest(message);
        log.info("[{}] MethodArgumentNotValidException: {}", exceptionResponse.getId(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException(final IllegalArgumentException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.badRequest(e.getMessage());
        log.info("[{}] IllegalArgumentException: {}", exceptionResponse.getId(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponse> handleException(final ValidationException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.unprocessableEntity(e.getMessage());
        log.info("[{}] ValidationException: {}", exceptionResponse.getId(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exceptionResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleException(final HttpRequestMethodNotSupportedException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.methodNotAllowed(e.getMessage());
        log.info("[{}] HttpRequestMethodNotSupportedException: {}", exceptionResponse.getId(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ExceptionResponse> handleException(final UnsupportedOperationException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.methodNotAllowed(e.getMessage());
        log.info("[{}] UnsupportedOperationException: {}", exceptionResponse.getId(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleException(final DataIntegrityViolationException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.internalServerError(e.getMessage());
        log.info("[{}] DataIntegrityViolationException: {}", exceptionResponse.getId(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleException(final RuntimeException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.internalServerError(e.getMessage());
        log.error("[{}] Runtime Exception ({}): {}", exceptionResponse.getId(), e.getClass(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(final Exception e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.internalServerError(e.getMessage());
        log.error("Common Exception ({}): {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

}
