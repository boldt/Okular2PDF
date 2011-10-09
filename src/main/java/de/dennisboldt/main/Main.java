package de.dennisboldt.main;

import java.io.File;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;

import de.dennisboldt.api.CreatePDF;
import de.dennisboldt.api.MergePDFs;
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
		String fileSource = directoryTemp + xmlContent.getDocumentFileName();
		String fileMetadata = directoryTemp + xmlContent.getMetadataFileName();
		String fileTemp = directoryTemp + "temp.pdf";
		String fileOutput = fileOkular  + ".annotated.pdf";

		// Step 3: Read the metadata.xml file
		XMLMetadataParser meta = new XMLMetadataParser(fileMetadata);

		// Step 4: Create temporarily PDF file
		PdfReader reader = new PdfReader(fileSource);
		Rectangle psize = reader.getPageSize(1);
		new CreatePDF(psize.getWidth(), psize.getHeight(), fileTemp, reader.getNumberOfPages(), meta.getPagesByType(4));

		// Step 5: Merge the temporarily PDF and the PDF file
		new MergePDFs(fileSource, fileTemp, fileOutput);

		// Step 6: Remove the unzipped files and the temporarily file.
		// TODO: Remove the unziped data
		System.out.println("The new PDF file has been created");
	}

}
