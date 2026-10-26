package io.github.rxue.investmentutility.marketquote;

import java.util.OptionalInt;

public class QuoteFetchException extends RuntimeException {
    private final Integer statusCode;

    public QuoteFetchException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
    }

    public QuoteFetchException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public OptionalInt statusCode() {
        return statusCode == null ? OptionalInt.empty() : OptionalInt.of(statusCode);
    }
}
