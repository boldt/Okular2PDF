package de.dennisboldt.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.dennisboldt.okular.Annotation;
import de.dennisboldt.okular.Page;

/**
 * Parsing of the metadata.xml
 *
 * @author Dennis Boldt
 * @see http://api.kde.org/4.7-api/kdegraphics-apidocs/okular/html/annotations_8cpp_source.html#l01727
 */
public class XMLMetadataParser {

	private Document dom = null;
	private List<Page> pages = new LinkedList<Page>();

	public XMLMetadataParser(String file) {
		System.out.println("(3) Parse the file " + file);
		parseXmlFile(file);
		parseDocument();
	}

	private void parseXmlFile(String file){
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

		NodeList pages = docEle.getElementsByTagName("page");
		//System.out.println("Pages: " + pages.getLength());

		if(pages != null && pages.getLength() > 0) {
			for(int i = 0 ; i < pages.getLength();i++) {

				//get the page element
				Element page = (Element) pages.item(i);
				Integer number = Integer.parseInt(page.getAttribute("number"));
				System.out.println("    Parse page number " + number);

				Page pageOkular = new Page(number);
				this.pages.add(pageOkular);

				// Get all annotations
				NodeList annotations = page.getElementsByTagName("annotation");
				if(annotations != null && annotations.getLength() > 0) {
					for(int j = 0 ; j < annotations.getLength(); j++) {
						Element annotation = (Element) annotations.item(j);
						Integer type = Integer.parseInt(annotation.getAttribute("type"));
						//System.out.println("  Annotation type:" + type);

						// Highlight
						if(type == 4) {

							// Get the boundary
							NodeList boundaries = annotation.getElementsByTagName("quad");
							if(boundaries != null && boundaries.getLength() > 0) {
								Element boundary = (Element) boundaries.item(0);
								/*
								 * The point of the quad are as follows:
								 *
								 * (ax,ay) --> (bx,by)
								 *                |
								 *                v
								 * (dx,dy) <-- (cx,cy)
								 *
								 * Thus:
								 * ax = dx => Left border (l)
								 * bx = cx => Right border (r)
								 * ay = by => Top border (t)
								 * dy = cy => Bottom border (b)
								 *
								 */
								Double l = Double.parseDouble(boundary.getAttribute("ax"));
								Double r = Double.parseDouble(boundary.getAttribute("cx"));
								Double t = Double.parseDouble(boundary.getAttribute("by"));
								Double b = Double.parseDouble(boundary.getAttribute("dy"));
								Annotation annotationOkular = new Annotation(l, r, t, b, type);
								//System.out.println("    l: " + l);
								//System.out.println("    r: " + r);
								//System.out.println("    t: " + t);
								//System.out.println("    b: " + b);
								pageOkular.addAnnotation(annotationOkular);
							}
						}
						// Type 1: Inline note
						else {

							NodeList boundaries = annotation.getElementsByTagName("boundary");
							if(boundaries != null && boundaries.getLength() > 0) {
								Element boundary = (Element) boundaries.item(0);
								Double l = Double.parseDouble(boundary.getAttribute("l"));
								Double r = Double.parseDouble(boundary.getAttribute("r"));
								Double t = Double.parseDouble(boundary.getAttribute("b"));
								Double b = Double.parseDouble(boundary.getAttribute("t"));

								System.out.println("Inline note found at " + l + " - " + r + " - " + b + " - " + t);

								NodeList escapedText = annotation.getElementsByTagName("escapedText");
								if(escapedText != null && escapedText.getLength() > 0) {
									Element textElement = (Element) escapedText.item(0);
									String text = textElement.getTextContent();
									Annotation annotationOkular = new Annotation(l, r, t, b, type, text);
									pageOkular.addAnnotation(annotationOkular);
								}
							}



						}
					}
				}
			}
		}
	}

	public List<Page> getPages() {
		return pages;
	}

	public Map<Integer, Page> getPagesByType(Integer type) {

		Map<Integer, Page> pagesByType = new HashMap<Integer, Page>();

		for (Page p : this.pages) {
			Page newPage = new Page(p.getNumber());
			List<Annotation> annotations = p.getAnnotations();
			for (Annotation annotation : annotations) {
				if(annotation.getType() == type) {
					newPage.addAnnotation(annotation);
				}
			}
			pagesByType.put(p.getNumber(), newPage);
		}
		return pagesByType;
	}
}
