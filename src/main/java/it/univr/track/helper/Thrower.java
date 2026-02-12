package it.univr.track.helper;

import it.univr.track.exception.ExceptionFactory;
import lombok.SneakyThrows;

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
}
