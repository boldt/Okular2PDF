package de.dennisboldt.api;

public class UnzipException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnzipException() {
		super();
	}

	public UnzipException(String message) {
		super(message);
	}

	public UnzipException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnzipException(Throwable cause) {
		super(cause);
	}

}
