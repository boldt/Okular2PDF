package de.dennisboldt.api;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
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

	float paperwidth = 612;
	float paperheight = 792;

	public CreatePDF(float paperwidth, float paperheight, String path,
			Integer pages, Map<Integer, Page> pagesList) throws FileNotFoundException,
			DocumentException {

		System.out.println("(4) Create a new PDF of size (" + paperwidth + "," + paperheight + ")");

		this.paperwidth = paperwidth;
		this.paperheight = paperheight;

		Rectangle rect = new Rectangle(paperwidth, paperheight);

		Document document = new Document(rect, 0, 0, 0, 0);
		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(path));
		document.open();
		System.out.println("    Draw the rectangles");
		for (int i = 0; i < pages; i++) {

			PdfContentByte under = writer.getDirectContentUnder();
			under.setRGBColorFill(0xFF, 0xD7, 0x00);
			Page pl = pagesList.get(i);
			if(pl != null) {
				List<Annotation> annotations = pl.getAnnotations();
				for (Annotation annotation : annotations) {
					int l = (int) (annotation.getL() * paperwidth);
					int r = (int) (annotation.getR() * paperwidth);
					int t = (int)(paperheight - (annotation.getT() * paperheight));
					int b = (int)(paperheight - (annotation.getB() * paperheight));
					under.rectangle(l, b, r - l, t - b);
				}
			}
			under.fill();
			document.newPage();
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
