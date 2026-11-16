package io.github.rxue.investmentutility.v2.marketquote;

public record Result<T>(T value, FailureReason failure) {
}
