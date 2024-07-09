package org.samvelaivazian.diagramtraversal.solution.parsers;

import org.camunda.bpm.engine.impl.util.json.JSONException;
import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.ModelParseException;
import org.samvelaivazian.diagramtraversal.solution.exceptions.ParseBpmnModelException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Parser class for BPMN models.
 */
public final class BpmnParser {

    /**
     * The key for extracting BPMN 2.0 XML from JSON.
     */
    private static final String BPMN_20_XML_KEY = "bpmn20Xml";

    /**
     * Extracts the BPMN 2.0 XML from a JSON response.
     *
     * @param jsonResponse The JSON response containing the BPMN 2.0 XML.
     * @return The BPMN 2.0 XML as a String.
     * @throws ParseBpmnModelException if there is an error parsing the JSON response.
     * @complexity Time: O(1), constant time to extract the BPMN 2.0 XML from the JSON response.
     *             Space: O(1), assuming constant space for the BPMN 2.0 XML.
     */
    public String getBpmn20XmlFromJson(final String jsonResponse) {
        final JSONObject responseJson = new JSONObject(jsonResponse);

        try {
            return responseJson.getString(BPMN_20_XML_KEY);
        } catch (final JSONException e) {
            throw new ParseBpmnModelException("Failed to get BPMN-20-XML from JSON: " + jsonResponse, e);
        }
    }

    /**
     * Parses the BPMN model from the BPMN 2.0 XML.
     *
     * @param xmlBpmnModel The BPMN 2.0 XML as a String.
     * @return The BPMN model instance.
     * @throws ParseBpmnModelException if there is an error parsing the BPMN XML.
     * @complexity Time: O(V + E), where V is the number of vertices (nodes) and E is the number of edges.
     *             Space: O(V + E), to store the BPMN model instance.
     */
    public BpmnModelInstance getBpmnModelFromXml(final String xmlBpmnModel) {
        final InputStream stream = new ByteArrayInputStream(xmlBpmnModel.getBytes());

        try {
            return Bpmn.readModelFromStream(stream);
        } catch (final ModelParseException e) {
            throw new ParseBpmnModelException("Failed to get BPMN Model from XML: " + xmlBpmnModel, e);
        }
    }

}
