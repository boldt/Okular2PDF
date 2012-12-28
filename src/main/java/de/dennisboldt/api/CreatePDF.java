package de.dennisboldt.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import de.dennisboldt.okular.Annotation;
import de.dennisboldt.okular.Page;

/**
 * Creates an PDF with the given Annotations
 *
 * @author Dennis Boldt
 *
 */
public class CreatePDF {

	// The width of the PDF
	private float paperwidth = 0;

	// The heigt of the PDF
	private float paperheight = 0;

	// Path to the temp folder
	private String tmpPath = null;

	// Number of pages of the creaeted PDF
	private Integer pages = 0;

	// parser for the the metadata.xml
	private XMLMetadataParser meta = null;

	private Logger logger = Logger.getLogger(CreatePDF.class);

	/**
	 * Inititialize the PDF creator
	 *
	 * @param paperwidth The width of the PDF which will be created
	 * @param paperheight The height of the PDF which will be created
	 * @param tmpPath The path to the temp folder
	 * @param pages Number of pages of the creaeted PDF
	 * @param meta parser for the the metadata.xml
	 * @throws FileNotFoundException TODO
	 * @throws DocumentException TODO
	 */
	public CreatePDF(float paperwidth, float paperheight, String tmpPath,
			Integer pages, XMLMetadataParser meta) throws FileNotFoundException,
			DocumentException {
		this.paperwidth = paperwidth;
		this.paperheight = paperheight;
		this.tmpPath = tmpPath;
		this.pages = pages;
		this.meta = meta;
	}

	/**
	 * Ths method does the annotation fot the given annotation type
	 *
	 * @param type An Okular annotation type
	 * @return The file, which is annotated
	 * @throws FileNotFoundException TODO
	 * @throws DocumentException TODO
	 */
	public File doAnnotation(AnnotationType type) throws FileNotFoundException, DocumentException {

		// Get all paged with the given metadata
		Map<Integer, Page> pagesList = meta.getPagesByType(type);

		// The file, which will be annotated in this step
		File annotatedFile = new File(tmpPath + type.toString() + ".pdf");
		this.logger.info("Create " + annotatedFile.getAbsolutePath()); // of size (" + paperwidth + "," + paperheight + ")

		// Create the canvas to be used to place stuff
		Rectangle rect = new Rectangle(paperwidth, paperheight);

		// Create the document
		Document document = new Document(rect, 0, 0, 0, 0);

		// PDFwriter, which creates a PDF file for a Focument
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(annotatedFile));

		// Opens the document
		document.open();
		for (int i = 0; i < pages; i++) {

			// Get the content, where to add the annotations
			PdfContentByte content = writer.getDirectContentUnder();

			// Get a singe page
			Page pl = pagesList.get(i);

			if(pl != null) {

				// Get all annotations for the selected page
				List<Annotation> annotations = pl.getAnnotations();
				for (Annotation annotation : annotations) {

					// Calculate the the exact positions, because l,t,t and b are
					// just given as percentages
					int l = (int) (annotation.getL() * paperwidth);
					int r = (int) (annotation.getR() * paperwidth);
					int t = (int)(paperheight - (annotation.getT() * paperheight));
					int b = (int)(paperheight - (annotation.getB() * paperheight));

					// Draw a yellow box
					if(type == AnnotationType.INLINE_NOTE || type ==  AnnotationType.YELLOW_HIGHLIGHTER) {
						// The color in #RRGGBB
						String color = annotation.getColor();
						if(color != null && color.length() == 7) {
							// Convert to int values
							int col_r = Integer.decode("0x" + color.substring(1, 3));
							int col_g = Integer.decode("0x" + color.substring(3, 5));
							int col_b = Integer.decode("0x" + color.substring(5, 7));
							content.setRGBColorFill(col_r, col_g, col_b);

						} else {
							// Standard color is yellow
							content.setRGBColorFill(0xFF, 0xFF, 0x00);
						}

						// Draw the rectangle at the calculated position
						content.rectangle(l, b, r - l, t - b);

						// If it is an inline note, than add the text to the box
						if(type == AnnotationType.INLINE_NOTE) {


							PdfContentByte directContent = writer.getDirectContent();
							// TODO: Make it dynamicaly based on th document
							directContent.setRGBColorFill(0x00, 0x00, 0x00);
					    	ColumnText ct = new ColumnText(directContent);

					        // Add the text
				        	Phrase p = new Phrase(annotation.getText());
				        	ct.setSimpleColumn(l+2, b, r, t, 14, Element.ALIGN_JUSTIFIED);
				        	ct.addText(p);
					    	ct.go();

					    	directContent.fill();
						}
					}

					// Add a simple PDF annotation
					else if (type == AnnotationType.PDF_NOTE) {
						float fl = Float.valueOf(l).floatValue();
						float fb = Float.valueOf(b).floatValue();
						float fr = Float.valueOf(r).floatValue();
						float ft = Float.valueOf(t).floatValue();
						// FIXME: Add an icon?
						// FIXME: Does not work!
						/*
						content.setRGBColorFill(0x00, 0x00, 0x00);
						PdfAnnotation anno = PdfAnnotation.createText(writer, new Rectangle(fl,fb,fr*2.f,2.f*ft), "Comment", annotation.getText(), false, null);
						writer.addAnnotation(anno);
						*/
					    PdfAnnotation text = PdfAnnotation.createText(writer, new Rectangle(fl,fb,fr*2.f,2.f*ft) ,"Fox", "The fox is quick", true, "Comment");
					    //Chunk fox = new Chunk("test").setAnnotation(text);
					    //writer.add(fox);
					    writer.addAnnotation(text);
						/*
						PdfAnnotation annotation = PdfAnnotation.createText(pdfStamper.getWriter(),
							    new Rectangle(x, valor, x+100f, valor+100f), "authors",
							    comentario.getComentario(), true, "Comment");
							annotation.setColor(Color.ORANGE);

							PdfAnnotation.createText(
                                    stamper.getWriter(),
                                    new Rectangle(30f, 750f, 80f, 800f),
                                    "inserted page", "This page is the title page.",
                                    true,
                                    null)

                           PdfAnnotation.createText(stamper.getWriter(), new Rectangle(30f, 750f, 80f, 800f), "inserted page", "This page is the title page.", true, null)
                   	    PdfAnnotation javascript = new PdfAnnotation(writer, 200f, 550f, 300f, 650f, PdfAction.javaScript("app.alert('hi');\r", writer));
						    Chunk dog = new Chunk("javascript").setAnnotation(javascript);
						    document.add(dog);
*/

					}
				}
			}

			// Finally, add the content
			if(type == AnnotationType.INLINE_NOTE) {
				content.fillStroke();
			} else {
				content.fill();
			}
			document.newPage();
		}
		document.close();
		return annotatedFile;
	}
}
