package io.github.rxue.investmentutility.v2.util;

public interface JsonContentConverter<T> {
    T to(String jsonContent);
}
