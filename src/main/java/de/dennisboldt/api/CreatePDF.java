package de.dennisboldt.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
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

	float paperwidth = 0;
	float paperheight = 0;
	String path = null;
	Integer pages = 0;
	XMLMetadataParser meta = null;

	public CreatePDF(float paperwidth, float paperheight, String path,
			Integer pages, XMLMetadataParser meta) throws FileNotFoundException,
			DocumentException {
		this.paperwidth = paperwidth;
		this.paperheight = paperheight;
		this.path = path;
		this.pages = pages;
		this.meta = meta;
	}

	public File doAnnotation(AnnotationType type) throws FileNotFoundException, DocumentException {
		Map<Integer, Page> pagesList = meta.getPagesByType(type);

		File f = new File(path + type.toString() + ".pdf");

		System.out.println("(4) Create a new PDF of size (" + paperwidth + "," + paperheight + ") at " + f.getAbsolutePath());

		Rectangle rect = new Rectangle(paperwidth, paperheight);

		Document document = new Document(rect, 0, 0, 0, 0);
		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(f));
		document.open();
		System.out.println("    Draw the rectangles");
		for (int i = 0; i < pages; i++) {
			//System.out.println("Page " + i);
			PdfContentByte under = writer.getDirectContentUnder();

			Page pl = pagesList.get(i);
			if(pl != null) {
				List<Annotation> annotations = pl.getAnnotations();
				for (Annotation annotation : annotations) {

					int l = (int) (annotation.getL() * paperwidth);
					int r = (int) (annotation.getR() * paperwidth);
					int t = (int)(paperheight - (annotation.getT() * paperheight));
					int b = (int)(paperheight - (annotation.getB() * paperheight));

					// Draw a yellow box
					if(type == AnnotationType.INLINE_NOTE || type ==  AnnotationType.YELLOW_HIGHLIGHTER) {
						// The color in #RRGGBB
						String color = annotation.getColor();
						if(color != null && color.length() == 7) {
							// Convert to int
							int col_r = Integer.decode("0x" + color.substring(1, 3));
							int col_g = Integer.decode("0x" + color.substring(3, 5));
							int col_b = Integer.decode("0x" + color.substring(5, 7));

							under.setRGBColorFill(col_r, col_g, col_b);

						} else {
							// Standard color is yellow
							under.setRGBColorFill(0xFF, 0xFF, 0x00);
						}

						under.rectangle(l, b, r - l, t - b);

						// Add some text to the box
						if(type == AnnotationType.INLINE_NOTE) {

							PdfContentByte cb = writer.getDirectContent();
							cb.setRGBColorFill(0x00, 0x00, 0x00); // TODO: Make it dynamicaly based on th document
					    	ColumnText ct = new ColumnText(cb);

					        // Add some test text
				        	Phrase p = new Phrase(annotation.getText());
				        	ct.setSimpleColumn(l+2, b, r, t, 14, Element.ALIGN_JUSTIFIED);
				        	ct.addText(p);
					    	ct.go();

					    	cb.fill();
						}
					} else if (type == AnnotationType.PDF_NOTE) {
						float fl = Float.valueOf(l).floatValue();
						float fb = Float.valueOf(b).floatValue();
						float fr = Float.valueOf(r).floatValue();
						float ft = Float.valueOf(t).floatValue();
						writer.addAnnotation(PdfAnnotation.createText(writer, new Rectangle(fl,fb,fr*2.f,2.f*ft), "Comment", annotation.getText(), false, null));
					}

					/*
					else if (type==4) {
						PdfContentByte cb = writer.getDirectContent();
						if (!isToplayer) {
							cb.setRGBColorFill(0x00, 0x00, 0x00); // TODO: Make it dynamicaly based on th document
							cb.fill();
						} else {
						}
					}
					*/
				}
			}

			if(type == AnnotationType.INLINE_NOTE) {
				under.fillStroke();
			} else {
				under.fill();
			}
			document.newPage();
			//System.out.println(b);
		}
		document.close();
		return f;
	}

	public static void drawRectangle(PdfContentByte content, float width,
			float height) {
		content.saveState();
		PdfGState state = new PdfGState();
		state.setFillOpacity(0.1f);
		content.setGState(state);
		content.rectangle(0, 0, width, height);
		content.fillStroke();
		content.restoreState();
	}

}
