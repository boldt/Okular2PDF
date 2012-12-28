package de.dennisboldt.api;

public class MimeTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public MimeTypeException() {
		super();
	}

	public MimeTypeException(String message) {
		super(message);
	}

	public MimeTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MimeTypeException(Throwable cause) {
		super(cause);
	}

}
