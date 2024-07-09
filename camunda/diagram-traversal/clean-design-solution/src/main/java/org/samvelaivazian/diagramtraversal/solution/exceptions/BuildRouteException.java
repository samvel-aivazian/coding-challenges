package org.samvelaivazian.diagramtraversal.solution.exceptions;

/**
 * Custom exception for errors when building the route.
 */
public final class BuildRouteException extends RuntimeException {

    /**
     * Constructs a new BuildRouteException with the specified detail message.
     *
     * @param message The detail message.
     */
    public BuildRouteException(final String message) {
        super(message);
    }

}
