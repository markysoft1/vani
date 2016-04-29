package org.vani.core.javascript;

import org.openqa.selenium.WebDriver;
import org.vani.core.annotation.DetectionScript;
import org.vani.core.annotation.JavaScript;
import org.vani.core.annotation.JavaScriptFunction;

@JavaScript(source = "classpath:org/vani/javascript/jquery-xhr-tracking.js")
public interface XhrTracking {
	@JavaScriptFunction(name = "window.vani.xhrTracking.hasRequestFor")
	boolean hasRequestFor(String url, long startInMillis, WebDriver webDriver);

	@DetectionScript("window.vani !== undefined && window.vani.xhrTracking !== undefined")
	public boolean isAvailable();

}
