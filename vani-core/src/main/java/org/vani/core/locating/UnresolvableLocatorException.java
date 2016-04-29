package org.vani.core.locating;

public class UnresolvableLocatorException extends RuntimeException {

	public UnresolvableLocatorException() {
	}

	public UnresolvableLocatorException(String message) {
		super(message);
	}

	public UnresolvableLocatorException(Throwable cause) {
		super(cause);
	}

	public UnresolvableLocatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnresolvableLocatorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
