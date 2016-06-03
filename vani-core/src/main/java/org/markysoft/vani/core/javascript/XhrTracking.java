package org.markysoft.vani.core.javascript;

import org.markysoft.vani.core.annotation.DetectionScript;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JavaScriptFunction;
import org.openqa.selenium.WebDriver;

@JavaScript(source = "classpath:jquery-xhr-tracking.js")
public interface XhrTracking {
	@JavaScriptFunction(name = "window.vani.xhrTracking.hasRequestFor")
	boolean hasRequestFor(String url, long startInMillis, WebDriver webDriver);

	@DetectionScript("window.vani !== undefined && window.vani.xhrTracking !== undefined")
	public boolean isAvailable();

}
