package de.dennisboldt.api;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parsing of the content.xml
 *
 * @author Dennis Boldt
 *
 */
public class XMLContentParser {

	private Document dom = null;
	private String documentFileName = null;
	private String metadataFileName = null;

	public XMLContentParser(File file) {
		System.out.println("(2) Parse the file " + file);
		parseXmlFile(file);
		parseDocument();
	}

	private void parseXmlFile(File file){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			this.dom = db.parse(file);
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void parseDocument() {
		//get the root element
		Element docEle = dom.getDocumentElement();
		this.documentFileName = getTextValue(docEle, "DocumentFileName");
		this.metadataFileName = getTextValue(docEle, "MetadataFileName");
	}

	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	public String getDocumentFileName() {
		return documentFileName;
	}

	public String getMetadataFileName() {
		return metadataFileName;
	}
}
