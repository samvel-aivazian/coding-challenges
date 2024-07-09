package org.samvelaivazian.diagramtraversal.solution;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.samvelaivazian.diagramtraversal.solution.exceptions.BuildRouteException;
import org.samvelaivazian.diagramtraversal.solution.exceptions.IncorrectNumberOfNodesException;
import org.samvelaivazian.diagramtraversal.solution.parsers.BpmnParser;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.samvelaivazian.diagramtraversal.solution.constants.FileConstants.PATH_TO_INVOICE_XML;

/**
 * Unit tests for the Solution class.
 */
@ExtendWith(MockitoExtension.class)
final class SolutionTest {

    /**
     * Mock instance of BpmnParser for testing purposes.
     */
    @Mock
    private BpmnParser mockParser;

    /**
     * The Solution instance to be tested.
     */
    @InjectMocks
    private Solution solution;

    @BeforeEach
    void setUp() {
        solution = new Solution(mockParser);
    }

    /**
     * Tests that the run method executes without throwing exceptions when valid arguments are provided.
     * This ensures that the method can parse the BPMN XML, build the model,
     * and find a path between valid start and end nodes.
     *
     * @throws IOException if reading the BPMN XML file fails
     */
    @Test
    void testRun_ValidArguments() throws IOException {
        final String startNodeId = "approveInvoice";
        final String endNodeId = "invoiceProcessed";
        final String[] args = {startNodeId, endNodeId};

        final String bpmnXml = Files.readString(PATH_TO_INVOICE_XML);
        final BpmnModelInstance mockModel = Mockito.mock(BpmnModelInstance.class);
        final FlowNode startNode = Mockito.mock(FlowNode.class);
        final FlowNode endNode = Mockito.mock(FlowNode.class);
        final SequenceFlow sequenceFlow = Mockito.mock(SequenceFlow.class);

        when(mockParser.getBpmn20XmlFromJson(anyString())).thenReturn(bpmnXml);
        when(mockParser.getBpmnModelFromXml(anyString())).thenReturn(mockModel);
        when(mockModel.getModelElementById(startNodeId)).thenReturn(startNode);
        when(mockModel.getModelElementById(endNodeId)).thenReturn(endNode);

        // Set up the mock to return a valid ID
        when(startNode.getId()).thenReturn(startNodeId);
        when(endNode.getId()).thenReturn(endNodeId);

        // Mock the getOutgoing method to return a list with one SequenceFlow
        when(startNode.getOutgoing()).thenReturn(List.of(sequenceFlow));
        when(sequenceFlow.getTarget()).thenReturn(endNode);

        assertDoesNotThrow(() -> solution.run(args));
    }

    /**
     * Tests that the run method throws an IncorrectNumberOfNodesException when too few arguments are provided.
     * This ensures that the method correctly validates the number of input arguments.
     */
    @Test
    void testRun_InvalidNumberOfArguments_TooFew() {
        final String[] args = {"startNodeId"};
        final Exception exception = assertThrows(IncorrectNumberOfNodesException.class, () -> solution.run(args));

        assertEquals("Must meet the requirement -> 'Usage: java -jar Solution.jar <startNodeId> <endNodeId>'", exception.getMessage());
    }

    /**
     * Tests that the run method throws an IncorrectNumberOfNodesException when too many arguments are provided.
     * This ensures that the method correctly validates the number of input arguments.
     */
    @Test
    void testRun_InvalidNumberOfArguments_TooMany() {
        final String[] args = {"startNodeId", "endNodeId", "extraArg"};
        final Exception exception = assertThrows(IncorrectNumberOfNodesException.class, () -> solution.run(args));

        assertEquals("Must meet the requirement -> 'Usage: java -jar Solution.jar <startNodeId> <endNodeId>'", exception.getMessage());
    }

    /**
     * Tests that the run method throws a BuildRouteException when the start node is invalid (not found in the model).
     * This ensures that the method correctly handles the case where the start node does not exist in the BPMN model.
     */
    @Test
    void testRun_InvalidStartNode() {
        final String[] args = {"invalidStartNode", "endNodeId"};

        final BpmnModelInstance mockModel = Mockito.mock(BpmnModelInstance.class);
        when(mockParser.getBpmn20XmlFromJson(anyString())).thenReturn("<definitions></definitions>");
        when(mockParser.getBpmnModelFromXml(anyString())).thenReturn(mockModel);
        when(mockModel.getModelElementById("invalidStartNode")).thenReturn(null);
        when(mockModel.getModelElementById("endNodeId")).thenReturn(null);

        assertThrows(BuildRouteException.class, () -> solution.run(args));
    }

    /**
     * Tests that the run method throws a BuildRouteException when the start node is valid but the end node
     * is invalid (not found in the model).
     * This ensures that the method correctly handles the case where the end node does not exist in the BPMN model.
     */
    @Test
    void testRun_ValidStartNode_InvalidEndNode() {
        final String[] args = {"startNodeId", "invalidEndNode"};

        final BpmnModelInstance mockModel = Mockito.mock(BpmnModelInstance.class);
        final FlowNode startNode = Mockito.mock(FlowNode.class);

        when(mockParser.getBpmn20XmlFromJson(anyString())).thenReturn("<definitions></definitions>");
        when(mockParser.getBpmnModelFromXml(anyString())).thenReturn(mockModel);
        when(mockModel.getModelElementById("startNodeId")).thenReturn(startNode);
        when(mockModel.getModelElementById("invalidEndNode")).thenReturn(null);

        assertThrows(BuildRouteException.class, () -> solution.run(args));
    }

    /**
     * Tests that the run method throws a BuildRouteException when there is no path from the start node to the end node.
     * This ensures that the method correctly handles the case where no valid path exists between the provided nodes.
     */
    @Test
    void testRun_NoPath() {
        final String[] args = {"startNodeId", "endNodeId"};

        final BpmnModelInstance mockModel = Mockito.mock(BpmnModelInstance.class);
        final FlowNode startNode = Mockito.mock(FlowNode.class);
        final FlowNode endNode = Mockito.mock(FlowNode.class);

        when(mockParser.getBpmn20XmlFromJson(anyString())).thenReturn("<definitions></definitions>");
        when(mockParser.getBpmnModelFromXml(anyString())).thenReturn(mockModel);
        when(mockModel.getModelElementById("startNodeId")).thenReturn(startNode);
        when(mockModel.getModelElementById("endNodeId")).thenReturn(endNode);
        when(startNode.getOutgoing()).thenReturn(Collections.emptyList()); // Ensure no outgoing flows

        assertThrows(BuildRouteException.class, () -> solution.run(args));
    }

    /**
     * Tests that the run method correctly handles the case where the target node's ID is already in the visited set.
     * This ensures that the method correctly handles the scenario where the DFS algorithm encounters a node
     * that has already been visited.
     *
     * @throws IOException if reading the BPMN XML file fails
     */
    @Test
    void testRun_VisitedContainsTargetNode() throws IOException {
        final String startNodeId = "approveInvoice";
        final String endNodeId = "invoiceNotProcessed";
        final String targetNodeId = "reviewSuccessful_gw";
        final String[] args = {startNodeId, endNodeId};

        final String bpmnXml = Files.readString(PATH_TO_INVOICE_XML);
        final BpmnModelInstance mockModel = Mockito.mock(BpmnModelInstance.class);
        final FlowNode startNode = Mockito.mock(FlowNode.class);
        final FlowNode targetNode = Mockito.mock(FlowNode.class);
        final FlowNode endNode = Mockito.mock(FlowNode.class);
        final SequenceFlow startNodeSequenceFlow = Mockito.mock(SequenceFlow.class);
        final SequenceFlow targetNodeSequenceFlow = Mockito.mock(SequenceFlow.class);
        final SequenceFlow endNodeSequenceFlow = Mockito.mock(SequenceFlow.class);

        // Set up the mock to return a valid ID
        when(startNode.getId()).thenReturn(startNodeId);
        when(endNode.getId()).thenReturn(endNodeId);
        when(targetNode.getId()).thenReturn(targetNodeId);

        // Mock the getOutgoing method to return a list with one SequenceFlow
        when(startNode.getOutgoing()).thenReturn(List.of(targetNodeSequenceFlow));
        when(targetNodeSequenceFlow.getTarget()).thenReturn(targetNode);
        when(targetNode.getOutgoing()).thenReturn(List.of(startNodeSequenceFlow, endNodeSequenceFlow));
        when(startNodeSequenceFlow.getTarget()).thenReturn(startNode);
        when(endNodeSequenceFlow.getTarget()).thenReturn(endNode);

        when(mockParser.getBpmn20XmlFromJson(anyString())).thenReturn(bpmnXml);
        when(mockParser.getBpmnModelFromXml(anyString())).thenReturn(mockModel);
        when(mockModel.getModelElementById(startNodeId)).thenReturn(startNode);
        when(mockModel.getModelElementById(endNodeId)).thenReturn(endNode);

        assertDoesNotThrow(() -> solution.run(args));
    }

}
