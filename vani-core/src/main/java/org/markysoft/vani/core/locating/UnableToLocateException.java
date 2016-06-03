package org.markysoft.vani.core.locating;

public class UnableToLocateException extends RuntimeException {
	private static final long serialVersionUID = 7606980031655900545L;

	public UnableToLocateException() {
	}

	public UnableToLocateException(String message) {
		super(message);
	}

	public UnableToLocateException(Throwable cause) {
		super(cause);
	}

	public UnableToLocateException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnableToLocateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
