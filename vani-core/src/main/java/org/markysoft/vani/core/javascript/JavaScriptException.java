package org.markysoft.vani.core.javascript;

@SuppressWarnings("serial")
public class JavaScriptException extends RuntimeException {

	public JavaScriptException() {
	}

	public JavaScriptException(String message) {
		super(message);
	}

	public JavaScriptException(Throwable cause) {
		super(cause);
	}

	public JavaScriptException(String message, Throwable cause) {
		super(message, cause);
	}

	public JavaScriptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
