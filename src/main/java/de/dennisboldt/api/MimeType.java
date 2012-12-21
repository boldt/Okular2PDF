package de.dennisboldt.api;

import java.io.File;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

/**
 *
 * @author Dennis Boldt
 *
 */
public class MimeType {

	public static String getMimeType(File file) throws java.io.IOException {
		// Works well
		try {
			MagicMatch match = Magic.getMagicMatch(file, false);
			return match.getMimeType();
		} catch (Exception e) {
		}

		return null;
	}

}
