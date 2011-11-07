package de.dennisboldt.api;

import java.io.FileOutputStream;
import java.text.DecimalFormat;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * Merges to PDFs such that one will be the background and one will be the
 * foreround. *
 *
 * @see: http://www.freeopenbook.com/pdf-hacks/pdfhks-CHP-6-SECT-18.html
 * @author Dennis Boldt
 */
public class MergePDFs {

	/**
	 * Merges to PDFs such that one will be the background and one will be the
	 * foreround.
	 *
	 * @param foreground
	 *            The PDF to be in the foreground
	 * @param background
	 *            The PDF to be in the background
	 * @param newFile
	 *            The new file
	 */
	public MergePDFs(String foreground, String background, String newFile) {

		System.out.println("(5) Merge " + foreground + " and " + background
				+ " to " + newFile);

		try {
			// the document we're watermarking
			PdfReader fg = new PdfReader(foreground);
			PdfReader bg = new PdfReader(background);

			// Check the amount of pages
			int fg_num_pages = fg.getNumberOfPages();
			int hg_num_pages = bg.getNumberOfPages();
			if (fg_num_pages != hg_num_pages) {
				// System.err.println("Numer of pages is not the same");
				// System.exit(0);
			}

			// Check, if the size of both PDFs is the same
			Rectangle fg_size = fg.getPageSize(1);
			Rectangle bg_size = bg.getPageSize(1);

			// Just use two fractional digits
			DecimalFormat df = new DecimalFormat("0.00");
			if (!df.format(fg_size.getHeight()).equals(df.format(bg_size.getHeight()))
					|| !df.format(fg_size.getWidth()).equals(df.format(bg_size.getWidth()))) {
				System.err
						.println("Geoemetry of the documents is not the same.");
				System.err.println(fg_size.getHeight() + "!="
						+ bg_size.getHeight());
				System.err.println(fg_size.getWidth() + "!="
						+ bg_size.getWidth());
				System.exit(0);

			}

			// the output document
			PdfStamper writer = new PdfStamper(fg,
					new FileOutputStream(newFile));

			// create a PdfTemplate from the first page of mark
			// (PdfImportedPage is derived from PdfTemplate)
			PdfImportedPage mark_page = null;
			for (int ii = 0; ii < fg_num_pages;) {
				++ii;
				mark_page = writer.getImportedPage(bg, ii);
				PdfContentByte contentByte = writer.getUnderContent(ii);
				contentByte.addTemplate(mark_page, 0, 0);
			}

			writer.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}

	}

}