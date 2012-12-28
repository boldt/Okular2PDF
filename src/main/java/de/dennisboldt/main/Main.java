package de.dennisboldt.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;

import de.dennisboldt.api.AnnotationType;
import de.dennisboldt.api.CreatePDF;
import de.dennisboldt.api.MergePDFs;
import de.dennisboldt.api.MimeType;
import de.dennisboldt.api.MimeTypeException;
import de.dennisboldt.api.Unzip;
import de.dennisboldt.api.UnzipException;
import de.dennisboldt.api.XMLContentParser;
import de.dennisboldt.api.XMLMetadataParser;

/**
 * The main file to be executed.
 *
 * @author Dennis Boldt
 */
public class Main {

	private Logger logger = Logger.getLogger(Main.class);

	public Main(String[] args) {
		if(args.length != 1) {
			this.logger.error("No Okular file was given.");
			System.exit(0);
		}

		File fileOkular = new File(args[0]);

		try {
			String mimeTypeFileOkular = MimeType.getMimeType(fileOkular);
			if(!"application/zip".equals(mimeTypeFileOkular)) {
				this.logger.warn(fileOkular.getName() + " is not an Okular file");
			}
		} catch (MimeTypeException e1) {
			e1.printStackTrace();
		}

		if(!fileOkular.getName().endsWith(".okular")) {
			this.logger.info("That is not an Okular file 2!");
			System.exit(0);
		}

		String directoryTemp = "/tmp/Okular2PDF/";

		// Step 1: Unzip the Okular file to temp
		try {
			new Unzip(fileOkular, directoryTemp);
		} catch (UnzipException e) {
			e.printStackTrace();
			System.exit(0);
		}

		// Step 2: Read the content.cml file to get the name of the PDF file and the
		// name of the metadata XML file
		if (!directoryTemp.endsWith("/")) {
			directoryTemp = directoryTemp + "/";
		}

		File f = new File(directoryTemp + "content.xml");
		if (!f.exists()) {
			try {
				throw new FileNotFoundException("The content.cml does not exist.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		XMLContentParser xmlContent = new XMLContentParser(f);
		String fileDocument = directoryTemp + xmlContent.getDocumentFileName();
		String fileMetadata = directoryTemp + xmlContent.getMetadataFileName();

		String type = null;
		try {
			type = MimeType.getMimeType(new File(fileDocument));
		} catch (MimeTypeException e) {
			e.printStackTrace();
		}

		String fileSource = null;
		// PDF files
		if("application/pdf".equals(type)){
			this.logger.info("PDF file detected");
			fileSource = fileDocument;
		} else if("application/postscript".equals(type)) {
			//this.logger.info("PS file");
			// PS files
			// TODO: ps2pdf
			/*
			String fileName = directoryTemp + "ps2pdf.pdf";
			String ps2pdf = "ps2pdf '" + fileDocument + "' '" + fileName +"'";
			Runtime rt = Runtime.getRuntime();
			Process p1  = rt.exec(ps2pdf);
			p1.waitFor();
			 */

			this.logger.info("PS not supported yet.");
			System.exit(0);
		} else if("application/x-dvi".equals(type)) {
			// DVI files
			// TODO: ps2pdf
			this.logger.info("DVI not supported yet.");
			System.exit(0);
		} else {
			this.logger.info("The mime type " + type + "not supported yet.");
			System.exit(0);
		}

		// Step 3: Read the metadata.xml file
		XMLMetadataParser meta = new XMLMetadataParser(fileMetadata);

		// Step 4: Create temporarily PDF files
		PdfReader reader = null;
		try {
			reader = new PdfReader(fileSource);
		} catch (IOException e) {
			this.logger.info("Cannot read " + new File(fileSource).getName());
			e.printStackTrace();
		}
		Rectangle psize = reader.getPageSize(1);

		// Yellow Highlighter
		File file_inline_note = null;
		File file_pdf_note = null;
		File file_yellow_highlighter = null;
		try {
			CreatePDF pdf = new CreatePDF(psize.getWidth(), psize.getHeight(), directoryTemp, reader.getNumberOfPages(), meta);
			file_inline_note = pdf.doAnnotation(AnnotationType.INLINE_NOTE);
			file_pdf_note = pdf.doAnnotation(AnnotationType.PDF_NOTE);
			file_yellow_highlighter = pdf.doAnnotation(AnnotationType.YELLOW_HIGHLIGHTER);
		} catch (FileNotFoundException e) {
			this.logger.warn("Cannot create the new file");
			e.printStackTrace();
			System.exit(0);
		} catch (DocumentException e) {
			this.logger.warn("Cannot create the new file");
			e.printStackTrace();
			System.exit(0);
		}

		File fileOutput = new File(fileOkular  + ".annotated.pdf");
		File tmp0 = new File(directoryTemp + "temp0.pdf");
		File tmp1 = new File(directoryTemp + "temp1.pdf");

		new MergePDFs(new File(fileSource), file_yellow_highlighter, tmp0);
		new MergePDFs(file_inline_note, tmp0, tmp1);
		new MergePDFs(file_pdf_note, tmp1, fileOutput);

		// Clean tempfolder
		File tmpDir = new File(directoryTemp);
		if(tmpDir.exists() && tmpDir.isDirectory()) {
			this.logger.info("Clean dir " + tmpDir.getAbsolutePath());
			try {
				FileUtils.deleteDirectory(tmpDir);
			} catch (IOException e) {
				this.logger.error("Cannot delete folder: " + tmpDir);
				e.printStackTrace();
			}
		}
		this.logger.info("The new PDF file has been created: " + fileOutput.getAbsolutePath());
	}

	public static void main(String[] args) throws Exception {
		new Main(args);
	}

}
