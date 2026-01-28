package it.univr.track.helper;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class Thrower {

    private Thrower() {
    }
    public static <T> T notNullArgumentException(T value, String message) {
        if (value == null)
            throw new IllegalArgumentException(message);
        return value;
    }

    public static void state(boolean condition, String message) {
        if (!condition)
            throw new IllegalStateException(message);
    }

    public static void argument(boolean condition, String message) {
        if (!condition)
            throw new IllegalArgumentException(message);
    }

    public static void notFound(String message) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    public static void badRequest(String message) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    public static void conflict(String message) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    public static void forbidden(String message) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    public static void unauthorized(String message) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
    }
}
