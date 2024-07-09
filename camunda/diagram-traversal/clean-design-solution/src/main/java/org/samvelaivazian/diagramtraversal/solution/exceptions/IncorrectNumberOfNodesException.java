package org.samvelaivazian.diagramtraversal.solution.exceptions;

/**
 * Custom exception for incorrect number of arguments.
 */
public final class IncorrectNumberOfNodesException extends IllegalArgumentException {

    /**
     * Constructs a new IncorrectNumberOfArgumentsException with the specified detail message.
     *
     * @param message The detail message.
     */
    public IncorrectNumberOfNodesException(final String message) {
        super(message);
    }

}
