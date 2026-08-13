package ru.yandex.practicum.order.client;

public sealed interface RemoteCallResult<T>
        permits RemoteCallResult.Success, RemoteCallResult.BusinessFailure, RemoteCallResult.TechnicalFailure {

    record Success<T>(T data) implements RemoteCallResult<T> {}

    record BusinessFailure<T>(String message) implements RemoteCallResult<T> {}

    record TechnicalFailure<T>(String message) implements RemoteCallResult<T> {}
}
