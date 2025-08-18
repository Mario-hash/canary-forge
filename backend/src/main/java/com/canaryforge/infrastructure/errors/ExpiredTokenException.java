package com.canaryforge.infrastructure.errors;

/** El token es válido pero está expirado. */
public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException() {
        super("Token expired");
    }

    public ExpiredTokenException(String msg) {
        super(msg);
    }

    public ExpiredTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
