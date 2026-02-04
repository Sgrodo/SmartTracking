package it.univr.track.helper;

import it.univr.track.exception.ExceptionFactory;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Function;

public final class Thrower {

    private Thrower() {
    }
    public static <T> T notNullArgumentException(T value, String message) {
        return notNullArgumentException(value, message, ExceptionFactory::invalidDataException);
    }

    @SneakyThrows
    public static <T> T notNullArgumentException(T value, String message, Function<String, ? extends Exception> factory) {
        if (value == null)
            throw factory.apply(message);
        return value;
    }

//    public static void state(boolean condition, String message) {
//        state(condition, message, ExceptionFactory::illegalArgumentException);
//    }
//
//    public static void state(boolean condition, String message, Function<String, ? extends RuntimeException> factory) {
//        if (!condition)
//            throw factory.apply(message);
//    }
//
//
//    public static void notFound(String message) {
//        throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
//    }
//
//    public static void badRequest(String message) {
//        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
//    }
//
//    public static void conflict(String message) {
//        throw new ResponseStatusException(HttpStatus.CONFLICT, message);
//    }
//
//    public static void forbidden(String message) {
//        throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
//    }
//
//    public static void unauthorized(String message) {
//        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
//    }
}
