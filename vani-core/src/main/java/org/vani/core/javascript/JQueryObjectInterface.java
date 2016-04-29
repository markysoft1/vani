package org.vani.core.javascript;

import org.openqa.selenium.WebDriver;
import org.vani.core.annotation.JavaScript;
import org.vani.core.annotation.JavaScriptFunction;
import org.vani.core.locating.JQueryElement;

@JavaScript(source = "classpath:jquery-object-attribute.js")
public interface JQueryObjectInterface {

	@JavaScriptFunction
	public <T> T objectAttribute(JQueryElement jQueryElement, String attr, WebDriver webDriver);
}
