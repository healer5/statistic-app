package com.profitsoft.parser.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class XmlStatisticWriter {

    public void write(Map<String, Long> statistics, String attributeName, Path outputDir) {
        String fileName = String.format("statistics_by_%s.xml", attributeName.toLowerCase());
        Path outputPath = outputDir.resolve(fileName);

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("statistics");
            doc.appendChild(rootElement);

            for (Map.Entry<String, Long> entry : statistics.entrySet()) {
                Element item = doc.createElement("item");
                rootElement.appendChild(item);

                Element value = doc.createElement("value");
                value.appendChild(doc.createTextNode(entry.getKey()));
                item.appendChild(value);

                Element count = doc.createElement("count");
                count.appendChild(doc.createTextNode(String.valueOf(entry.getValue())));
                item.appendChild(count);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(outputPath.toString()));
            transformer.transform(source, result);

            System.out.printf(String.valueOf(outputPath.toAbsolutePath()));

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}