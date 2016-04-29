package org.vani.core.javascript;

@SuppressWarnings("serial")
public class JavaScriptReadException extends JavaScriptException {

	public JavaScriptReadException() {
	}

	public JavaScriptReadException(String message) {
		super(message);
	}

	public JavaScriptReadException(Throwable cause) {
		super(cause);
	}

	public JavaScriptReadException(String message, Throwable cause) {
		super(message, cause);
	}

	public JavaScriptReadException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
