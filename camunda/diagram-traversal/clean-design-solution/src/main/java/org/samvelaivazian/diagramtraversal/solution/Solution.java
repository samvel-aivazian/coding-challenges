package org.samvelaivazian.diagramtraversal.solution;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.samvelaivazian.diagramtraversal.solution.exceptions.BuildRouteException;
import org.samvelaivazian.diagramtraversal.solution.exceptions.IncorrectNumberOfNodesException;
import org.samvelaivazian.diagramtraversal.solution.parsers.BpmnParser;
import org.samvelaivazian.diagramtraversal.solution.utils.HttpUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Solution class for finding a path in a BPMN diagram from a start node to an end node.
 */
public final class Solution {

    /**
     * The BpmnParser instance used for parsing BPMN models.
     */
    private final BpmnParser bpmnParser;

    /**
     * Constructor to initialize the Solution with a BpmnParser.
     *
     * @param bpmnParser The BpmnParser instance to use.
     */
    public Solution(BpmnParser bpmnParser) {
        this.bpmnParser = bpmnParser;
    }

    /**
     * Main execution method to run the solution.
     *
     * @param nodeIds Array of node IDs containing the start and end node IDs.
     * @throws BuildRouteException if no path is found from startNodeId to endNodeId.
     * @throws IncorrectNumberOfNodesException if the number of arguments is incorrect.
     * @complexity Time: O(V + E), where V is the number of vertices (nodes) and E is the number of edges.
     *             Space: O(V), due to the stack, visited set, and path map which can store up to V nodes.
     */
    public void run(final String[] nodeIds) {
        validateNumberOfProvidedNodes(nodeIds);

        final String startNodeId = nodeIds[0];
        final String endNodeId = nodeIds[1];
        final String invoiceApprovalXML = fetchInvoiceApprovalXML();
        final BpmnModelInstance bpmnModelInstance = bpmnParser.getBpmnModelFromXml(invoiceApprovalXML);
        final List<String> route = buildRouteFromModelByStartAndEndNode(bpmnModelInstance, startNodeId, endNodeId);

        printRoute(startNodeId, endNodeId, route);
    }

    /**
     * Validates the number of provided node IDs.
     *
     * @param nodeIds The node IDs provided.
     * @throws IncorrectNumberOfNodesException if the number of node IDs is not exactly 2.
     * @complexity Time: O(1), constant time to check the number of node IDs.
     *             Space: O(1), no additional space required.
     */
    private void validateNumberOfProvidedNodes(final String[] nodeIds) {
        if (nodeIds.length != 2) {
            throw new IncorrectNumberOfNodesException("Must meet the requirement -> 'Usage: java -jar Solution.jar <startNodeId> <endNodeId>'");
        }
    }

    /**
     * Fetches the BPMN XML from the remote server.
     *
     * @return BPMN XML as a String.
     * @complexity Time: O(1) for creating and sending the HTTP request. The actual time depends on network latency.
     *             Space: O(1), assuming constant space for the HTTP request and response.
     */
    private String fetchInvoiceApprovalXML() {
        final String url = "https://n35ro2ic4d.execute-api.eu-central-1.amazonaws.com/prod/engine-rest/process-definition/key/invoice/xml";
        final String responseData = HttpUtils.fetchResponse(url);

        return bpmnParser.getBpmn20XmlFromJson(responseData);
    }

    /**
     * Builds the route from the start node to the end node in the BPMN model.
     *
     * @param bpmnModelInstance The BPMN model instance.
     * @param startNodeId       ID of the start node.
     * @param endNodeId         ID of the end node.
     * @return List of node IDs representing the path from the start node to the end node.
     * @throws BuildRouteException if no path is found from startNodeId to endNodeId.
     * @complexity Time: O(V + E), where V is the number of vertices (nodes) and E is the number of edges.
     *             Space: O(V), due to the stack, visited set, and path map which can store up to V nodes.
     */
    private List<String> buildRouteFromModelByStartAndEndNode(final BpmnModelInstance bpmnModelInstance,
                                                              final String startNodeId,
                                                              final String endNodeId) {
        final FlowNode startNode = bpmnModelInstance.getModelElementById(startNodeId);
        final FlowNode endNode = bpmnModelInstance.getModelElementById(endNodeId);
        validateIfModelNodesExist(startNodeId, endNodeId, startNode, endNode);

        return buildRoute(startNode, endNode);
    }

    /**
     * Validates if the provided start and end nodes exist in the BPMN model.
     *
     * @param startNodeId ID of the start node.
     * @param endNodeId   ID of the end node.
     * @param startNode   The start node object.
     * @param endNode     The end node object.
     * @throws BuildRouteException if either the start node or the end node does not exist.
     * @complexity Time: O(1), constant time to check if nodes are null.
     *             Space: O(1), no additional space required.
     */
    private void validateIfModelNodesExist(final String startNodeId, final String endNodeId,
                                           final FlowNode startNode, final FlowNode endNode) {
        if (startNode == null || endNode == null) {
            throw new BuildRouteException("Failed to obtain FlowNode from startNodeId '" +
                    startNodeId + "' or/and " + "endNodeId '" + endNodeId + "'"
            );
        }
    }

    /**
     * Builds the route from the start node to the end node using an iterative DFS approach.
     *
     * @param startNode The start node.
     * @param endNode   The end node.
     * @return List of node IDs representing the path from the start node to the end node.
     * @throws BuildRouteException if no path is found from startNodeId to endNodeId.
     * @complexity Time: O(V + E), where V is the number of vertices (nodes) and E is the number of edges.
     *             Space: O(V), due to the stack, visited set, and path map which can store up to V nodes.
     */
    private List<String> buildRoute(final FlowNode startNode, final FlowNode endNode) {
        // Initialize stack for DFS and map to track the path
        final Deque<FlowNode> stack = new ArrayDeque<>();
        final Map<FlowNode, FlowNode> pathMap = new HashMap<>();
        final Set<String> visited = new HashSet<>();

        // Push the start node onto the stack and mark it as visited
        stack.push(startNode);
        visited.add(startNode.getId());
        pathMap.put(startNode, null);

        // Perform iterative DFS
        while (!stack.isEmpty()) {
            final FlowNode currentNode = stack.pop();

            // If the end node is found, reconstruct the path and return
            if (currentNode.equals(endNode)) {
                return reconstructPath(endNode, pathMap);
            }

            // Traverse all outgoing edges
            for (SequenceFlow outgoing : currentNode.getOutgoing()) {
                final FlowNode targetNode = outgoing.getTarget();

                if (!visited.contains(targetNode.getId())) {
                    stack.push(targetNode);
                    visited.add(targetNode.getId());
                    pathMap.put(targetNode, currentNode);
                }
            }
        }

        // If no path is found, throw an exception
        throw new BuildRouteException("Failed to build route from model");
    }

    /**
     * Reconstructs the path from the end node to the start node using the path map.
     *
     * @param endNode The end node.
     * @param pathMap Map storing the path from each node to its predecessor.
     * @return List of node IDs representing the path from the start node to the end node.
     * @complexity Time: O(V), where V is the number of vertices (nodes), because we potentially visit each node once.
     *             Space: O(V), due to the route list which stores up to V nodes.
     */
    private List<String> reconstructPath(final FlowNode endNode, final Map<FlowNode, FlowNode> pathMap) {
        final List<String> route = new ArrayList<>();
        FlowNode currentNode = endNode;

        // Backtrack from end node to start node using the path map
        while (currentNode != null) {
            route.addFirst(currentNode.getId());
            currentNode = pathMap.get(currentNode);
        }

        return route;
    }

    /**
     * Prints the route from the start node to the end node.
     *
     * @param startNodeId ID of the start node.
     * @param endNodeId   ID of the end node.
     * @param route       List of node IDs representing the path.
     * @complexity Time: O(V), where V is the number of vertices (nodes) in the route.
     *             Space: O(1), only basic variable storage.
     */
    private void printRoute(final String startNodeId, final String endNodeId, final List<String> route) {
        System.out.println("The path from " + startNodeId + " to " + endNodeId + " is: " + route);
    }

}
