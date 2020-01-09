package com.xml.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChangeXML {

    private String exception = "";

    public String getException() {
        return exception;
    }

    public void change(Data data, File directory) {
        String and = "&amp;\\s";
        String pattern = "yyyyMMddHHmmssSSS";
        String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));

        String sender = data.getSender();
        String receiver = data.getRecName();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputFile = classloader.getResourceAsStream(sender + ".xml");

        String outputFileSource = directory + File.separator + "TS_" + sender + "_SBDH;"
                + receiver.toUpperCase().replaceAll("[\\\\/:*?\"<>|]", "") + ".xml";

        File outputFile = new File(outputFileSource);
        InputSource in = new InputSource(inputFile);
        in.setEncoding("UTF-8");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document;
        try {
            document = dbf.newDocumentBuilder().parse(in);
        } catch (Exception e) {
            exception = "Cannot parse input file" + "\n" + e.getMessage();
            return;
        }

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();

        try {
            XPathExpression id = xpath.compile("//Receiver/Identifier");
            XPathExpression date = xpath.compile("//DocumentIdentification/InstanceIdentifier");
            XPathExpression partyId = xpath.compile("//BuyerParty/Party/PartyIdentification/ID");
            XPathExpression name = xpath.compile("//BuyerParty/Party/PartyName/Name");
            XPathExpression street = xpath.compile("//BuyerParty/Party/Address/StreetName");
            XPathExpression city = xpath.compile("//BuyerParty/Party/Address/CityName");
            XPathExpression code = xpath.compile("//BuyerParty/Party/Address/PostalZone");
            XPathExpression orgNr = xpath.compile("//BuyerParty/Party/PartyTaxScheme/CompanyID");

            Node idNode = (Node) id.evaluate(document, XPathConstants.NODE);
            Node instanceId = (Node) date.evaluate(document, XPathConstants.NODE);
            Node partyIdNode = (Node) partyId.evaluate(document, XPathConstants.NODE);
            Node nameNode = (Node) name.evaluate(document, XPathConstants.NODE);
            Node streetNode = (Node) street.evaluate(document, XPathConstants.NODE);
            Node cityNode = (Node) city.evaluate(document, XPathConstants.NODE);
            Node codeNode = (Node) code.evaluate(document, XPathConstants.NODE);
            Node orgNrNode = (Node) orgNr.evaluate(document, XPathConstants.NODE);

            idNode.setTextContent(data.getRecId());
            instanceId.setTextContent(dateNow);
            partyIdNode.setTextContent(data.getRecId());
            nameNode.setTextContent(receiver.toUpperCase().replace("&\\s", and));
            streetNode.setTextContent(data.getRecStreet().toUpperCase());
            cityNode.setTextContent(data.getRecCity().toUpperCase());
            codeNode.setTextContent(data.getRecZip());
            orgNrNode.setTextContent(data.getRecOrgNr());
        } catch (Exception e) {
            exception = "Cannot change data in XML" + "\n" + e.getMessage();
            return;
        }

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(document), new StreamResult(outputFile));
        } catch (Exception e) {
            exception = "Cannot parse the output file" + "\n" + e.getMessage();
        }
    }
}