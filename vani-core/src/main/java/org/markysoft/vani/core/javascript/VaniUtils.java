package org.markysoft.vani.core.javascript;

import org.markysoft.vani.core.annotation.DetectionScript;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JavaScriptFunction;
import org.openqa.selenium.WebDriver;

@JavaScript(source = "classpath:org/markysoft/vani/javascript/vani-utils.js")
public interface VaniUtils {
	@JavaScriptFunction(name = "window.vani.uuid4")
	String uuid4();

	@DetectionScript("window.vani !== undefined")
	public boolean isAvailable();

	/**
	 * This method will return the value of provided javascript variable.
	 * 
	 * @param name
	 *            name of javascript variable
	 * @return
	 */
	@JavaScriptFunction("window[arguments[0]]")
	public <T> T get(String name, WebDriver webDriver);

	/**
	 * This method will set the specified variable to given value
	 * 
	 * @param variableName
	 * @param value
	 * @param webDriver
	 */
	@JavaScriptFunction("window[arguments[0]]=arguments[1]")
	<T> void set(String variableName, T value, WebDriver webDriver);
}
