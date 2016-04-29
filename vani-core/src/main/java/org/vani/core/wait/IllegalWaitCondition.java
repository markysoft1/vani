package org.vani.core.wait;

public class IllegalWaitCondition extends RuntimeException {

	public IllegalWaitCondition() {
	}

	public IllegalWaitCondition(String message) {
		super(message);
	}

	public IllegalWaitCondition(Throwable cause) {
		super(cause);
	}

	public IllegalWaitCondition(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalWaitCondition(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
