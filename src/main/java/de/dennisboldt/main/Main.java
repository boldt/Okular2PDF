package de.dennisboldt.main;

import java.io.File;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;

import de.dennisboldt.api.CreatePDF;
import de.dennisboldt.api.MergePDFs;
import de.dennisboldt.api.MimeType;
import de.dennisboldt.api.Unzip;
import de.dennisboldt.api.XMLContentParser;
import de.dennisboldt.api.XMLMetadataParser;

/**
 * The main file to be executed.
 *
 * @author Dennis Boldt
 */
public class Main {

	public static void main(String[] args) throws Exception {

		if(args.length != 1) {
			System.err.println("No Okular file was given.");
			System.exit(0);
		}

		String fileOkular = args[0];

		System.out.println(MimeType.getMimeType(fileOkular));


		String directoryTemp = "/tmp/Okular2PDF/";

		// Step 1: Unzip the Okular file to temp
		new Unzip(fileOkular, directoryTemp);

		// Step 2: Read the content.cml file to get the name of the PDF file and the
		// name of the metadata XML file
		if (!directoryTemp.endsWith("/")) {
			directoryTemp = directoryTemp + "/";
		}

		File f = new File(directoryTemp + "content.xml");
		if (!f.exists()) {
			throw new Exception("The content.cml does not exist.");
		}

		XMLContentParser xmlContent = new XMLContentParser(f);
		String fileDocument = directoryTemp + xmlContent.getDocumentFileName();
		String fileMetadata = directoryTemp + xmlContent.getMetadataFileName();
		String fileTempHighlighter = directoryTemp + "yellow_highlighter.pdf";
		String fileTempInlineNote = directoryTemp + "inline_note.pdf";
		String fileTemp = directoryTemp + "temp.pdf";
		String fileOutput = fileOkular  + ".annotated.pdf";

		String type = MimeType.getMimeType(fileDocument);

		String fileSource = null;
		// PDF files
		if("application/pdf".equals(type)){
			fileSource = fileDocument;
		} else if("application/postscript".equals(type)) {
			// PS files
			// TODO: ps2pdf
			/*
			String fileName = directoryTemp + "ps2pdf.pdf";
			String ps2pdf = "ps2pdf '" + fileDocument + "' '" + fileName +"'";
			Runtime rt = Runtime.getRuntime();
			Process p1  = rt.exec(ps2pdf);
			p1.waitFor();
			 */

			System.out.println("PS not supported yet.");
			System.exit(0);
		} else if("application/x-dvi".equals(type)) {
			// DVI files
			// TODO: ps2pdf
			System.out.println("DVI not supported yet.");
			System.exit(0);
		} else {
			System.out.println("The mime type " + type + "not supported yet.");
			System.exit(0);
		}

		// Step 3: Read the metadata.xml file
		XMLMetadataParser meta = new XMLMetadataParser(fileMetadata);

		// Step 4: Create temporarily PDF files
		PdfReader reader = new PdfReader(fileSource);
		Rectangle psize = reader.getPageSize(1);

		// Yellow Highlighter
		new CreatePDF(psize.getWidth(), psize.getHeight(), fileTempHighlighter, reader.getNumberOfPages(), meta, 4);

		// Inline notes
		new CreatePDF(psize.getWidth(), psize.getHeight(), fileTempInlineNote, reader.getNumberOfPages(), meta, 1);

		// Step 5: Merge the temporarily PDFs and the PDF file
		new MergePDFs(fileSource, fileTempHighlighter, fileTemp);
		new MergePDFs(fileTempInlineNote, fileTemp, fileOutput);

		// Step 8: Remove the unzipped files and the temporarily file.
		// TODO: Remove the unziped data
		// TODO: Remove temp data
		System.out.println("The new PDF file has been created");
	}

}
