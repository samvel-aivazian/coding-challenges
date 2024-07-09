package org.samvelaivazian.diagramtraversal.solution.utils;

import org.junit.jupiter.api.Test;
import org.samvelaivazian.diagramtraversal.solution.exceptions.FetchResponseDataException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the HttpUtils class.
 */
final class HttpUtilsTest {

    /**
     * Tests that fetchResponse method successfully returns response data for a valid URL.
     * This ensures that the method correctly sends an HTTP request and returns the response body.
     */
    @Test
    void testFetchResponse_ValidUrl() {
        final String url = "https://httpbin.org/get";

        assertDoesNotThrow(() -> {
            final String response = HttpUtils.fetchResponse(url);

            assertNotNull(response);
        });
    }

    /**
     * Tests that fetchResponse method throws a FetchResponseDataException for an invalid URL.
     * This ensures that the method correctly handles and throws an exception for invalid URL input.
     */
    @Test
    void testFetchResponse_InvalidUrl() {
        final String url = "http://invalid.url";

        assertThrows(FetchResponseDataException.class, () -> HttpUtils.fetchResponse(url));
    }

    /**
     * Tests that fetchResponse method throws a FetchResponseDataException for a URL that causes a timeout.
     * This ensures that the method correctly handles and throws an exception for requests that time out.
     */
    @Test
    void testFetchResponse_Timeout() {
        final String url = "https://httpbin.org/delay/10"; // simulate delay

        assertThrows(FetchResponseDataException.class, () -> HttpUtils.fetchResponse(url));
    }

    /**
     * Tests that createHttpRequest method throws a FetchResponseDataException for a malformed URL.
     * This ensures that the method correctly handles and throws an exception for malformed URL input.
     */
    @Test
    void testCreateHttpRequest_InvalidUrl() {
        final String url = "invalid-url"; // malformed URL
        final FetchResponseDataException exception = assertThrows(
                FetchResponseDataException.class,
                () -> HttpUtils.fetchResponse(url)
        );

        assertEquals("Failed to create HTTP request from " + url, exception.getMessage());
    }

}
