package de.dennisboldt.main;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;

import de.dennisboldt.api.AnnotationType;
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

		File fileOkular = new File(args[0]);

		/*
		if("application/zip".trim().equals(MimeType.getMimeType(fileOkular).trim())) {
			System.out.println("application/zip".trim());
			System.out.println(MimeType.getMimeType(fileOkular));
			System.out.println("That is not an Okular file 1!");
			System.exit(0);
		}
		*/

		if(!fileOkular.getName().endsWith(".okular")) {
			System.out.println("That is not an Okular file 2!");
			System.exit(0);
		}

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

		String type = MimeType.getMimeType(new File(fileDocument));

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
		CreatePDF pdf = new CreatePDF(psize.getWidth(), psize.getHeight(), directoryTemp, reader.getNumberOfPages(), meta);
		File file_inline_note = pdf.doAnnotation(AnnotationType.INLINE_NOTE);
		File file_pdf_note = pdf.doAnnotation(AnnotationType.PDF_NOTE);
		File file_yellow_highlighter = pdf.doAnnotation(AnnotationType.YELLOW_HIGHLIGHTER);
		File fileOutput = new File(fileOkular  + ".annotated.pdf");
		File tmp0 = new File(directoryTemp + "temp0.pdf");
		File tmp1 = new File(directoryTemp + "temp1.pdf");

		System.out.println("(5) Merge files");

		new MergePDFs(new File(fileSource), file_yellow_highlighter, tmp0);
		new MergePDFs(file_inline_note, tmp0, tmp1);
		new MergePDFs(file_pdf_note, tmp1, fileOutput);

		// Clean tempfolder
		File tmpDir = new File(directoryTemp);
		if(tmpDir.exists() && tmpDir.isDirectory()) {
			System.out.println("(5) Clean dir " + tmpDir.getAbsolutePath());
			FileUtils.deleteDirectory(tmpDir);
		}
		System.out.println();
		System.out.println("The new PDF file has been created:");
		System.out.println(fileOutput.getAbsolutePath());
	}

}
