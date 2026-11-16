package io.github.rxue.investmentutility.v2.util;

public interface JsonNodeConverter<T> {
    T toData(String jsonContent);
}
