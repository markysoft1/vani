package org.markysoft.vani.core.javascript;

import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JavaScriptFunction;
import org.markysoft.vani.core.locating.JQueryElement;
import org.openqa.selenium.WebDriver;

@JavaScript(source = "classpath:jquery-object-attribute.js")
public interface JQueryObjectInterface {

	@JavaScriptFunction
	public <T> T objectAttribute(JQueryElement jQueryElement, String attr, WebDriver webDriver);
}
