package gov.eeoc.complainantportal.common;

public class DocumentExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DocumentExistException() {
		
	}

	public DocumentExistException(String message) {
		super(message);
		
	}

	public DocumentExistException(Throwable cause) {
		super(cause);
		
	}

	public DocumentExistException(String message, Throwable cause) {
		super(message, cause);
		
	}

}

