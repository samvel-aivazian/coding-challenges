package org.samvelaivazian.diagramtraversal.solution.utils;

import org.samvelaivazian.diagramtraversal.solution.exceptions.FetchResponseDataException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Utility class for making HTTP requests.
 */
public final class HttpUtils {

    /**
     * The default timeout for HTTP requests.
     */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private HttpUtils() {
    }

    /**
     * Fetches the response from the given URL.
     *
     * @param url The URL to fetch the response from.
     * @return The response body as a String.
     * @throws FetchResponseDataException if there is an error fetching the data.
     * @complexity Time: O(1) for creating and sending the HTTP request. The actual time depends on network latency.
     *             Space: O(1), assuming constant space for the HTTP request and response.
     */
    public static String fetchResponse(final String url) {
        final HttpClient client = createHttpClient();
        final HttpRequest request = createHttpRequest(url);
        final HttpResponse<String> httpResponse = sendHttpRequest(client, request);

        return httpResponse.body();
    }

    /**
     * Creates an HttpClient with the default timeout.
     *
     * @return An instance of HttpClient.
     * @complexity Time: O(1), constant time to create the HttpClient.
     *             Space: O(1), constant space for the HttpClient.
     */
    private static HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(DEFAULT_TIMEOUT)
                .build();
    }

    /**
     * Creates an HttpRequest for the given URL.
     *
     * @param url The URL to create the request for.
     * @return An instance of HttpRequest.
     * @throws FetchResponseDataException if the URL is invalid.
     * @complexity Time: O(1), constant time to create the HttpRequest.
     *             Space: O(1), constant space for the HttpRequest.
     */
    private static HttpRequest createHttpRequest(final String url) {
        try {
            return HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(DEFAULT_TIMEOUT)
                    .GET()
                    .build();
        } catch (final IllegalArgumentException e) {
            throw new FetchResponseDataException("Failed to create HTTP request from " + url, e);
        }
    }

    /**
     * Sends the HTTP request and returns the response.
     *
     * @param client  The HttpClient to use.
     * @param request The HttpRequest to send.
     * @return The HttpResponse received.
     * @throws FetchResponseDataException if there is an error sending the request.
     * @complexity Time: O(1) for sending the HTTP request. The actual time depends on network latency.
     *             Space: O(1), assuming constant space for the HttpResponse.
     */
    private static HttpResponse<String> sendHttpRequest(final HttpClient client, final HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (final IOException | InterruptedException e) {
            throw new FetchResponseDataException("Failed to send request from " + request.uri(), e);
        }
    }

}
