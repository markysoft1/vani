package org.markysoft.vani.core;

public class IllegalVaniFieldException extends RuntimeException {
	private static final long serialVersionUID = -573462013808358644L;

	public IllegalVaniFieldException() {
		super();
	}

	public IllegalVaniFieldException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IllegalVaniFieldException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalVaniFieldException(String message) {
		super(message);
	}

	public IllegalVaniFieldException(Throwable cause) {
		super(cause);
	}

}
