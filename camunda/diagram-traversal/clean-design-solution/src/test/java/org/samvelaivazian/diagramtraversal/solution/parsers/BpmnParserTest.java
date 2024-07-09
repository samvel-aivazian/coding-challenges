package org.samvelaivazian.diagramtraversal.solution.parsers;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.samvelaivazian.diagramtraversal.solution.exceptions.ParseBpmnModelException;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.samvelaivazian.diagramtraversal.solution.constants.FileConstants.PATH_TO_INVOICE_XML;

/**
 * Unit tests for the BpmnParser class.
 */
@ExtendWith(MockitoExtension.class)
final class BpmnParserTest {

    /**
     * The BpmnParser instance to be tested.
     */
    @InjectMocks
    private BpmnParser bpmnParser;

    /**
     * Tests that getBpmn20XmlFromJson method successfully extracts BPMN XML from a valid JSON string.
     * This ensures that the method correctly parses and returns BPMN XML for valid JSON input.
     */
    @Test
    void testGetBpmn20XmlFromJson_ValidJson() {
        final String jsonResponse = "{\"bpmn20Xml\": \"<definitions></definitions>\"}";
        final String bpmnXml = bpmnParser.getBpmn20XmlFromJson(jsonResponse);

        assertEquals("<definitions></definitions>", bpmnXml);
    }

    /**
     * Tests that getBpmn20XmlFromJson method throws a ParseBpmnModelException for JSON without the bpmn20Xml key.
     * This ensures that the method correctly handles and throws an exception for JSON input missing the required key.
     */
    @Test
    void testGetBpmn20XmlFromJson_InvalidJson() {
        final String jsonResponse = "{\"invalidKey\": \"value\"}";

        assertThrows(ParseBpmnModelException.class, () -> bpmnParser.getBpmn20XmlFromJson(jsonResponse));
    }

    /**
     * Tests that getBpmnModelFromXml method successfully parses valid BPMN XML.
     * This ensures that the method correctly parses and returns a non-null BpmnModelInstance for valid XML input.
     *
     * @throws IOException if reading the BPMN XML file fails
     */
    @Test
    void testGetBpmnModelFromXml_ValidXml() throws IOException {
        // Read the BPMN XML from the test resources folder
        final String bpmnXml = Files.readString(PATH_TO_INVOICE_XML);

        assertDoesNotThrow(() -> {
            final BpmnModelInstance modelInstance = bpmnParser.getBpmnModelFromXml(bpmnXml);

            assertNotNull(modelInstance);
        });
    }

    /**
     * Tests that getBpmnModelFromXml method throws a ParseBpmnModelException for invalid BPMN XML.
     * This ensures that the method correctly handles and throws an exception for invalid XML input.
     */
    @Test
    void testGetBpmnModelFromXml_InvalidXml() {
        final String bpmnXml = "<invalid>";

        assertThrows(ParseBpmnModelException.class, () -> bpmnParser.getBpmnModelFromXml(bpmnXml));
    }

}
