package com.canaryforge.infrastructure.errors;

/**
 * El token es inválido/tampered o su firma no coincide.
 * Para endpoints /c y /p lo trataremos como 404 para no filtrar información.
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("Invalid token");
    }

    public InvalidTokenException(String msg) {
        super(msg);
    }

    public InvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
