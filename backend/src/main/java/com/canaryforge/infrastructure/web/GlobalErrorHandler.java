package com.canaryforge.infrastructure.web;

import com.canaryforge.infrastructure.errors.ExpiredTokenException;
import com.canaryforge.infrastructure.errors.InvalidTokenException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handler global de errores para WebFlux.
 *
 * Reglas:
 * - /c/* y /p/*:
 * * token inválido -> 404
 * * token expirado -> 410
 * - Resto de endpoints:
 * * validación/entrada inválida -> 400
 * * token inválido -> 400
 * * token expirado -> 410
 * - Por defecto -> 500
 */
@RestControllerAdvice
public class GlobalErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    /** DTO de error que devolvemos. */
    public record ApiError(
            Instant timestamp,
            String path,
            int status,
            String error,
            String code, // código corto: e.g. TOKEN_INVALID, TOKEN_EXPIRED, VALIDATION_ERROR
            String message,
            Map<String, Object> details) {
    }

    private boolean isCeboEndpoint(String path) {
        return path != null && (path.startsWith("/c/") || path.startsWith("/p/"));
    }

    private ResponseEntity<ApiError> build(ServerWebExchange ex, HttpStatus status, String code, String message,
            Map<String, Object> details) {
        String path = ex.getRequest().getPath().value();
        ApiError body = new ApiError(Instant.now(), path, status.value(), status.getReasonPhrase(), code, message,
                details);
        return ResponseEntity.status(status)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private ResponseEntity<ApiError> build(ServerWebExchange ex, HttpStatus status, String code, String message) {
        return build(ex, status, code, message, null);
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<ApiError> handleExpired(ExpiredTokenException e, ServerWebExchange ex) {
        // 410 Gone siempre para expirado
        return build(ex, HttpStatus.GONE, "TOKEN_EXPIRED", e.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalid(InvalidTokenException e, ServerWebExchange ex) {
        // Si es /c o /p → 404 (discreción). Si no, 400.
        HttpStatus st = isCeboEndpoint(ex.getRequest().getPath().value()) ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;
        return build(ex, st, "TOKEN_INVALID", e.getMessage());
    }

    // Algunas librerías/adapter lanzan IllegalArgumentException o
    // SecurityException.
    // Las normalizamos para no tocar aún los adapters.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException e, ServerWebExchange ex) {
        if (isCeboEndpoint(ex.getRequest().getPath().value())) {
            // En /c y /p preferimos 404 si el motivo es "token inválido"
            return build(ex, HttpStatus.NOT_FOUND, "TOKEN_INVALID", msgOrDefault(e, "Invalid token"));
        }
        return build(ex, HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiError> handleSecurity(SecurityException e, ServerWebExchange ex) {
        String m = Optional.ofNullable(e.getMessage()).orElse("").toLowerCase();
        if (m.contains("expired")) {
            return handleExpired(new ExpiredTokenException(e.getMessage(), e), ex);
        }
        // Firma incorrecta → inválido
        return handleInvalid(new InvalidTokenException(e.getMessage(), e), ex);
    }

    // Validación (DTOs @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgNotValid(MethodArgumentNotValidException e, ServerWebExchange ex) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("fields", e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> Optional.ofNullable(fe.getDefaultMessage()).orElse("invalid"),
                        (a, b) -> a,
                        LinkedHashMap::new)));
        return build(ex, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", details);
    }

    // Validación programática (@Validated en parámetros, etc.)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException e, ServerWebExchange ex) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("violations", e.getConstraintViolations().stream()
                .map(v -> Map.of("property", v.getPropertyPath().toString(), "message", v.getMessage()))
                .toList());
        return build(ex, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Constraint violations", details);
    }

    // Errores de binding/cuerpo mal formado (JSON mal, etc.)
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ApiError> handleInput(ServerWebInputException e, ServerWebExchange ex) {
        return build(ex, HttpStatus.BAD_REQUEST, "BAD_REQUEST", msgOrDefault(e, "Invalid request body"));
    }

    // Si alguien lanza explícitamente ResponseStatusException, respetamos su
    // status.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleRse(ResponseStatusException e, ServerWebExchange ex) {
        var code = e.getStatusCode();
        HttpStatus http = (code instanceof HttpStatus h)
                ? h
                : HttpStatus.valueOf(code.value());

        String reason = (e.getReason() != null && !e.getReason().isBlank())
                ? e.getReason()
                : http.getReasonPhrase();

        return build(ex,
                http,
                http.is4xxClientError() ? "BAD_REQUEST" : "ERROR",
                msgOrDefault(e, reason));
    }

    // Fallback genérico → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception e, ServerWebExchange ex) {
        log.error("Unhandled exception", e);
        return build(ex, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error");
    }

    private String msgOrDefault(Throwable t, String def) {
        String m = t.getMessage();
        return (m == null || m.isBlank()) ? def : m;
    }
}
