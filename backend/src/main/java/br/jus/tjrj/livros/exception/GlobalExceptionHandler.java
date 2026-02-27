package br.jus.tjrj.livros.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiError(Instant.now(), 404, "Not Found", ex.getMessage(), req.getRequestURI(), List.of())
        );
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ApiError> handleValidation(ValidacaoException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiError(Instant.now(), 400, "Bad Request", ex.getMessage(), req.getRequestURI(), List.of())
        );
    }

    private ApiError.FieldError mapFieldError(FieldError fe) {
        return new ApiError.FieldError(fe.getField(), fe.getDefaultMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        // Ex: violação FK, unique, etc.
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiError(Instant.now(), 409, "Conflict", "Database constraint violation", req.getRequestURI(), List.of())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiError(Instant.now(), 500, "Internal Server Error", "Unexpected error", req.getRequestURI(), List.of())
        );
    }
}
