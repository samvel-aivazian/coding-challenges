import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Solution {

    public static void main(final String[] args) {
        if (args.length != 2) {
            System.err.println(
                    "Must meet the requirement -> 'Usage: java -jar Solution.jar <input file> <output file>'"
            );
            System.exit(-1);
        }

        final String invoiceApprovalXML = fetchInvoiceApprovalXML();
        if (invoiceApprovalXML == null) {
            System.err.println("No response received");
            System.exit(-1);
        }

        final BpmnModelInstance bpmnModelInstance = parseBpmnModel(invoiceApprovalXML);
        if (bpmnModelInstance == null) {
            System.err.println("Failed to parse BPMN model from invoice approval XML");
            System.exit(-1);
        }

        final String startNodeId = args[0];
        final String endNodeId = args[1];
        final String route = buildRouteFromModelByStartAndEndNode(bpmnModelInstance, startNodeId, endNodeId);
        if (route == null) {
            System.err.println("Failed to build route from model");
            System.exit(-1);
        }

        printRoute(startNodeId, endNodeId, route);
    }

    private static String fetchInvoiceApprovalXML() {
        final String url
                = "https://n35ro2ic4d.execute-api.eu-central-1.amazonaws.com/prod/engine-rest/process-definition/key/invoice/xml";

        try (final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final String responseBody = response.body();
            final JSONObject responseJson = new JSONObject(responseBody);

            return responseJson.optString("bpmn20Xml");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to fetch data from " + url);
            return null;
        }
    }

    private static BpmnModelInstance parseBpmnModel(final String xml) {
        final InputStream stream = new ByteArrayInputStream(xml.getBytes());

        return Bpmn.readModelFromStream(stream);
    }

    private static String buildRouteFromModelByStartAndEndNode(final BpmnModelInstance bpmnModelInstance,
                                                               final String startNodeId,
                                                               final String endNodeId) {
        final FlowNode startNode = bpmnModelInstance.getModelElementById(startNodeId);
        final FlowNode endNode = bpmnModelInstance.getModelElementById(endNodeId);
        if (startNode == null || endNode == null) {
            return null;
        }

        final List<String> route = new ArrayList<>();
        final Set<String> visitedFlowNodes = new HashSet<>();

        return findPath(startNode, endNode, route, visitedFlowNodes) ? route.toString() : null;
    }

    private static boolean findPath(final FlowNode currentNode, final FlowNode endNode,
                                    final List<String> route, final Set<String> visited) {
        route.add(currentNode.getId());
        visited.add(currentNode.getId());

        if (currentNode.equals(endNode)) {
            return true;
        }

        for (final SequenceFlow outgoing : currentNode.getOutgoing()) {
            final FlowNode targetNode = outgoing.getTarget();

            if (!visited.contains(targetNode.getId())) {
                final boolean pathFound = findPath(targetNode, endNode, route, visited);

                if (pathFound) {
                    return true;
                }
            }
        }

        route.removeLast();
        visited.remove(currentNode.getId());

        return false;
    }

    private static void printRoute(final String startNodeId, final String endNodeId, final String route) {
        System.out.println("The path from " + startNodeId + " to " + endNodeId + " is: " + route);
    }

}
