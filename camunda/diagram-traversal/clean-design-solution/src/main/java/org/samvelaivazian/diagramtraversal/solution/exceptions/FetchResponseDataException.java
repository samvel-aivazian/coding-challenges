package org.samvelaivazian.diagramtraversal.solution.exceptions;

/**
 * Custom exception for errors when fetching response data.
 */
public final class FetchResponseDataException extends RuntimeException {

    /**
     * Constructs a new FetchResponseDataException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause.
     */
    public FetchResponseDataException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
