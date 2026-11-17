package io.github.rxue.investmentutility.v2.util;

public sealed interface Result<T> {
    record Success<T>(T value) implements Result<T> {}
    record ResponseError<T>(int errorStatusCode, String description) implements Result<T> {

    }
    record TechnicalError<T>(String description, Throwable cause) implements Result<T> {

    }
}
