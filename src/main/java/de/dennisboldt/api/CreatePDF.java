package de.dennisboldt.api;

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
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfAnnotation;

import de.dennisboldt.okular.Annotation;
import de.dennisboldt.okular.Page;

/**
 * Creates an PDF with the given Annotations
 *
 * @author Dennis Boldt
 *
 */
public class CreatePDF {

	float paperwidth = 612;
	float paperheight = 792;

	public CreatePDF(float paperwidth, float paperheight, String path,
			Integer pages, XMLMetadataParser meta, Integer type, boolean isToplayer) throws FileNotFoundException,
			DocumentException {

		Map<Integer, Page> pagesList = meta.getPagesByType(type);

		System.out.println("(4) Create a new PDF of size (" + paperwidth + "," + paperheight + ") at " + path);

		this.paperwidth = paperwidth;
		this.paperheight = paperheight;

		Rectangle rect = new Rectangle(paperwidth, paperheight);

		Document document = new Document(rect, 0, 0, 0, 0);
		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(path));
		document.open();
		System.out.println("    Draw the rectangles");
		for (int i = 0; i < pages; i++) {
			//System.out.println("Page " + i);
			PdfContentByte under = writer.getDirectContentUnder();

			Page pl = pagesList.get(i);
			if(pl != null) {
				List<Annotation> annotations = pl.getAnnotations();
				for (Annotation annotation : annotations) {

					// The color in #RRGGBB
					String color = annotation.getColor();
					if(color != null && color.length() == 7) {
						// Convert to int
						int r = Integer.decode("0x" + color.substring(1, 3));
						int g = Integer.decode("0x" + color.substring(3, 5));
						int b = Integer.decode("0x" + color.substring(5, 7));

						under.setRGBColorFill(r, g, b);

					} else {
						// Standard color is yellow
						under.setRGBColorFill(0xFF, 0xFF, 0x00);
					}

					int l = (int) (annotation.getL() * paperwidth);
					int r = (int) (annotation.getR() * paperwidth);
					int t = (int)(paperheight - (annotation.getT() * paperheight));
					int b = (int)(paperheight - (annotation.getB() * paperheight));
					under.rectangle(l, b, r - l, t - b);

					// Add some text...
					if(type == 1) {

						PdfContentByte cb = writer.getDirectContent();
						cb.setRGBColorFill(0x00, 0x00, 0x00); // TODO: Make it dynamicaly based on th document
				    	ColumnText ct = new ColumnText(cb);

						System.out.println("overlay=" + annotation.getText());

				        // Add some test text
			        	Phrase p = new Phrase(annotation.getText());
			        	ct.setSimpleColumn(l+2, b, r, t, 14, Element.ALIGN_JUSTIFIED);
			        	ct.addText(p);
				    	ct.go();

				    	cb.fill();

				    	/*
				    	float fl = Float.valueOf(l).floatValue();
				    	float fb = Float.valueOf(b).floatValue();
				    	float fr = Float.valueOf(r-l).floatValue();
				    	float ft = Float.valueOf(t-b).floatValue();

						// Fillstroke
				        PdfContentByte cb = writer.getDirectContent();
				        cb.setColorStroke(Color.black);
				        cb.rectangle(fl, fb, fr, ft);
				        cb.stroke();
				    	 */
					} else if (type==4) {
						PdfContentByte cb = writer.getDirectContent();
						if (!isToplayer) {
							cb.setRGBColorFill(0x00, 0x00, 0x00); // TODO: Make it dynamicaly based on th document
							cb.fill();
						} else {
							float fl = Float.valueOf(l).floatValue();
							float fb = Float.valueOf(b).floatValue();
							float fr = Float.valueOf(r).floatValue();
							float ft = Float.valueOf(t).floatValue();

							writer.addAnnotation(PdfAnnotation.createText(writer, new Rectangle(fl,fb,fr*2.f,2.f*ft), "Comment", annotation.getText(), false, null));
							System.out.println("annotation=" + annotation.getText());
						}
					}
				}
			}

			if(type == 1) {
				// Add border-lines
				under.fillStroke();
			} else {
				if (!isToplayer)under.fill();
			}
			boolean b = document.newPage();
			//System.out.println(b);
		}
		document.close();
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
