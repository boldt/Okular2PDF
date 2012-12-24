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
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
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

						String color = null;
						String author = null;
						String contents = null;
						NodeList basees = annotation.getElementsByTagName("base");
						Element base = null;
						if(basees != null && basees.getLength() == 1) {
							base = (Element) basees.item(0);
							color = base.getAttribute("color");
							author = base.getAttribute("author");
							contents = base.getAttribute("contents");
						}

						Annotation annotationOkular = null;

						// Highlight
						if(type == 4) {

							// Get the boundary
							NodeList boundaries = annotation.getElementsByTagName("quad");
							// TODO: equals 1 ?
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

								annotationOkular = new Annotation(l, r, t, b, AnnotationType.YELLOW_HIGHLIGHTER);
								//System.out.println("    l: " + l);
								//System.out.println("    r: " + r);
								//System.out.println("    t: " + t);
								//System.out.println("    b: " + b);
							}
						}
						// Type 1: Inline note
						else if (type == 1){

							NodeList boundaries = annotation.getElementsByTagName("boundary");
							if(boundaries != null && boundaries.getLength() > 0) {
								Element boundary = (Element) boundaries.item(0);
								Double l = Double.parseDouble(boundary.getAttribute("l"));
								Double r = Double.parseDouble(boundary.getAttribute("r"));
								Double t = Double.parseDouble(boundary.getAttribute("b"));
								Double b = Double.parseDouble(boundary.getAttribute("t"));

								// Get the summary
								String summary = null;
								NodeList windows = annotation.getElementsByTagName("window");
								Element window = null;
								if(windows != null && windows.getLength() == 1) {
									window = (Element) windows.item(0);
									summary = window.getAttribute("summary");
								}

								if("Inline Note".equals(summary)) {
									NodeList escapedText = annotation.getElementsByTagName("escapedText");
									if(escapedText != null && escapedText.getLength() > 0) {
										Element textElement = (Element) escapedText.item(0);
										String text = textElement.getTextContent();
										annotationOkular = new Annotation(l, r, t, b, AnnotationType.INLINE_NOTE);
										annotationOkular.setText(text);
									}
								} else if("Note".equals(summary)) {
									if (author != null && contents != null) {
										annotationOkular = new Annotation(l, r+0.3, t+0.1, b, AnnotationType.PDF_NOTE);
										annotationOkular.setText("Author: " + author + "\n" + contents);
									}
								} else {
									System.out.println("WARNING: Unknown summary type (" + summary + ")");
									System.out.println("XML:");
									try {
										// @see: http://stackoverflow.com/a/1219806/605890
										Document document = annotation.getOwnerDocument();
										DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
										LSSerializer serializer = domImplLS.createLSSerializer();
										String str = serializer.writeToString(annotation);
										System.out.println(str);
									} catch (Exception e) {
										System.out.println("Exception: XML not readable.");
									} catch (Error e) {
										System.out.println("Error: XML not readable.");
									}
									continue;
								}
							}
						} else {
							System.out.println("WARNING: Unknown annotation type (" + type + ")");
							System.out.println("XML:");
							try {
								// @see: http://stackoverflow.com/a/1219806/605890
								Document document = annotation.getOwnerDocument();
								DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
								LSSerializer serializer = domImplLS.createLSSerializer();
								String str = serializer.writeToString(annotation);
								System.out.println(str);
							} catch (Exception e) {
								System.out.println("Exception: XML not readable.");
							} catch (Error e) {
								System.out.println("Error: XML not readable.");
							}
							continue;
						}

						// Set up the color
						annotationOkular.setColor(color);

						// Add the annotation
						pageOkular.addAnnotation(annotationOkular);
					}
				}
			}
		}
	}

	public List<Page> getPages() {
		return pages;
	}

	public Map<Integer, Page> getPagesByType(AnnotationType type) {

		Map<Integer, Page> pagesByType = new HashMap<Integer, Page>();

		for (Page p : this.pages) {
			Page newPage = new Page(p.getNumber());
			List<Annotation> annotations = p.getAnnotations();
			for (Annotation annotation : annotations) {
				if(annotation != null && annotation.getType() == type) {
					newPage.addAnnotation(annotation);
				}
			}
			pagesByType.put(p.getNumber(), newPage);
		}
		return pagesByType;
	}
}
