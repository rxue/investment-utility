package io.github.rxue.investmentutility.marketquote;

import java.util.OptionalInt;

public class QuoteFetchException extends RuntimeException {
    private final int responseStatusCode;
    public QuoteFetchException(String message, int responseStatusCode) {
        super(message);
        this.responseStatusCode = responseStatusCode;
    }
    public OptionalInt statusCode() {
        if (responseStatusCode == 0) return OptionalInt.empty();
        return OptionalInt.of(responseStatusCode);
    }
}
