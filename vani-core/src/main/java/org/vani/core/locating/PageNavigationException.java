package org.vani.core.locating;

public class PageNavigationException extends RuntimeException {
	private static final long serialVersionUID = -5528843809513443418L;

	public PageNavigationException() {
		super();
	}

	public PageNavigationException(String msg) {
		super(msg);
	}

	public PageNavigationException(Throwable cause) {
		super(cause);
	}

	public PageNavigationException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public PageNavigationException(String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(msg, cause, enableSuppression, writableStackTrace);
	}

}
