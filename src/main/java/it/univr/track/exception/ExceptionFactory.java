package it.univr.track.exception;

public class ExceptionFactory {

    public static IllegalArgumentException illegalArgumentException(String message){
        return new IllegalArgumentException(message);
    }

    public static NotFoundException notFoundException(String message){
        return new NotFoundException(message);
    }

    public static InvalidDataException invalidDataException(String message){
        return new InvalidDataException(message);
    }

}
