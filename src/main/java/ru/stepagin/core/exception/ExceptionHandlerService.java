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
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerService {

    private void logException(Exception e, String responseId) {
        final String msg = "Exception handled [" + responseId + "]: " + e.getClass().getName() + ": " + e.getMessage();
        log.info(msg);
    }

    /*
     * Обработка исключений, вызваных jakarta.validation.constraints.*
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(final MethodArgumentNotValidException e) {
        final String message = Arrays.stream(Objects.requireNonNull(e.getDetailMessageArguments())).toList().get(1).toString();
        ExceptionResponse exceptionResponse = ExceptionResponse.badRequest(message);
        logException(e, exceptionResponse.getId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler({
            MissingServletRequestPartException.class,
            IllegalArgumentException.class,
            HttpRequestMethodNotSupportedException.class,
            BadFileException.class,
            NoChangesException.class,
            UserAlreadyExistsException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequestException(final Exception e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.badRequest(e.getMessage());
        logException(e, exceptionResponse.getId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler({
            IllegalActionException.class,
            UserNotFoundException.class,
            ImageNotFoundException.class
    })
    public ResponseEntity<ExceptionResponse> handleNotFoundException(final Exception e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.notFound(e.getMessage());
        logException(e, exceptionResponse.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleForbiddenException(final AccessDeniedException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.notFound(e.getMessage());
        logException(e, exceptionResponse.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ExceptionResponse> handleException(final UnsupportedOperationException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.methodNotAllowed(e.getMessage());
        logException(e, exceptionResponse.getId());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(exceptionResponse);
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponse> handleException(final ValidationException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.unprocessableEntity(e.getMessage());
        logException(e, exceptionResponse.getId());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exceptionResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleSqlException(final DataIntegrityViolationException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.internalServerError("Internal Server Error");
        logException(e, exceptionResponse.getId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(final RuntimeException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.internalServerError(e.getMessage());
        log.error("[{}] Runtime Exception ({}): {}", exceptionResponse.getId(), e.getClass(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleCommonException(final Exception e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.internalServerError(e.getMessage());
        log.error("Common Exception ({}): {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

}
