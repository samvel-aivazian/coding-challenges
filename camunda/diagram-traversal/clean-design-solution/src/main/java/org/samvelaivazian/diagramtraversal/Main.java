package org.samvelaivazian.diagramtraversal;

import org.samvelaivazian.diagramtraversal.solution.Solution;
import org.samvelaivazian.diagramtraversal.solution.exceptions.IncorrectNumberOfNodesException;
import org.samvelaivazian.diagramtraversal.solution.parsers.BpmnParser;

/**
 * Main class to run the solution.
 */
public final class Main {

    /**
     * Main method to run the solution.
     *
     * @param args The command line arguments.
     * @throws IncorrectNumberOfNodesException if the number of arguments is incorrect.
     */
    public static void main(final String[] args) {
        final BpmnParser bpmnParser = new BpmnParser();
        final Solution solution = new Solution(bpmnParser);

        solution.run(args);
    }

}
