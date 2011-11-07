package de.dennisboldt.api;

import java.net.FileNameMap;
import java.net.URLConnection;

/**
 *
 * @author Dennis Boldt
 * @see http://www.rgagnon.com/javadetails/java-0487.html
 *
 */
public class MimeType {

	public static String getMimeType(String fileUrl) throws java.io.IOException {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String type = fileNameMap.getContentTypeFor(fileUrl);
		return type;
	}

}
