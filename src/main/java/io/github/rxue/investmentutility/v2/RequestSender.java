package io.github.rxue.investmentutility.v2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class RequestSender {
    // Yahoo answers 429 to the default Java user agent, and to Linux browser user agents as well
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private final HttpClient httpClient;
    public RequestSender(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    public HttpResponse<String> send(URI uri) throws InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("User-Agent", USER_AGENT)
                .timeout(TIMEOUT)
                .GET()
                .build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
