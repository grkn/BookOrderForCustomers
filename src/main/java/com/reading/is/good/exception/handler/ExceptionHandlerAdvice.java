package com.reading.is.good.exception.handler;

import com.reading.is.good.exception.BadRequestException;
import com.reading.is.good.exception.ExceptionResponse;
import com.reading.is.good.exception.NotFoundException;
import com.reading.is.good.resource.ErrorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ErrorResource> handleAccessDenied(AccessDeniedException ex) {
        LOGGER.error("User does not have any permission to this request.", ex);
        return ResponseEntity.status(ExceptionResponse.UNAUTHORIZED.getStatus())
                .body(new ErrorResource(ExceptionResponse.UNAUTHORIZED.getCode(),
                        ExceptionResponse.UNAUTHORIZED.getMessage()));
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ErrorResource> handleBadRequest(BadRequestException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(ExceptionResponse.BAD_REQUEST.getStatus())
                .body(new ErrorResource(ExceptionResponse.BAD_REQUEST.getCode(),
                        String.format(ExceptionResponse.BAD_REQUEST.getMessage(), ex.getMessage())));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResource> handleMethodArgumentValidException(MethodArgumentNotValidException ex) {
        Optional<String> fieldName = findErrorFieldName(ex);
        Optional<Object> fieldValue = findErrorValue(ex);
        LOGGER.error(String.format("Parameter %s can not be %s",
                fieldName.orElse("unknown"), fieldValue.orElse("null or empty")), ex);
        return ResponseEntity.status(ExceptionResponse.BAD_REQUEST.getStatus())
                .body(new ErrorResource(ExceptionResponse.BAD_REQUEST.getCode(),
                        String.format(ExceptionResponse.BAD_REQUEST.getMessage(),
                                String.format("Parameter %s can not be %s",
                                        fieldName.orElse("unknown"), fieldValue.orElse("null or empty")))));
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorResource> handleNotFoundException(NotFoundException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(ExceptionResponse.NOT_FOUND.getStatus())
                .body(new ErrorResource(ExceptionResponse.NOT_FOUND.getCode(),
                        String.format(ExceptionResponse.NOT_FOUND.getMessage(), ex.getMessage())));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResource> handleGeneralException(Exception ex) {
        LOGGER.error("Unknown Exception occurred", ex);
        return ResponseEntity.badRequest().body(new ErrorResource(ExceptionResponse.GENERAL.getCode(),
                String.format(ExceptionResponse.GENERAL.getMessage() + "Detail message is: %s", ex.getMessage())));
    }


    private Optional<String> findErrorFieldName(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .findFirst();
    }

    private Optional<Object> findErrorValue(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map((fieldError) -> Optional.ofNullable(fieldError.getRejectedValue()))
                .findFirst().orElseGet(Optional::empty);

    }
}
