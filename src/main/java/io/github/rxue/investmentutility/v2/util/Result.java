package io.github.rxue.investmentutility.v2.util;

public record Result<T>(T value, FailureReason failure) {
}
