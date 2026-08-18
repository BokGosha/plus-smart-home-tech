package ru.yandex.practicum.order.exception;

public class RemoteServiceException extends RuntimeException {

    public RemoteServiceException(String message) {
        super(message);
    }
}
