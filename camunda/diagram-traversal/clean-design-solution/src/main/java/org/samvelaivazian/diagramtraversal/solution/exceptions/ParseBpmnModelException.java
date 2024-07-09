package org.samvelaivazian.diagramtraversal.solution.exceptions;

/**
 * Custom exception for errors when parsing a BPMN model.
 */
public final class ParseBpmnModelException extends RuntimeException {

    /**
     * Constructs a new ParseBpmnModelException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause.
     */
    public ParseBpmnModelException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
