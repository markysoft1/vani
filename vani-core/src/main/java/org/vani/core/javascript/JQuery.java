package org.vani.core.javascript;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.vani.core.annotation.DetectionScript;
import org.vani.core.annotation.GlobalReference;
import org.vani.core.annotation.JavaScript;
import org.vani.core.annotation.JavaScriptDependency;
import org.vani.core.annotation.JavaScriptFunction;
import org.vani.core.annotation.JsCallFunction;
import org.vani.core.annotation.JsFunctionArguments;
import org.vani.core.annotation.JsFunctionName;
import org.vani.core.locating.JQueryElement;

@JavaScriptDependency({ VaniUtils.class, XhrTracking.class })
@JavaScript(source = "classpath:jquery-2.2.1.js")
public interface JQuery extends JQueryRegexSelectorPlugin, JQueryObjectInterface {

	@DetectionScript("typeof jQuery !== 'undefined'")
	public boolean isAvailable();

	@JsCallFunction("classpath:vani-jquery-call.js")
	public String call(@GlobalReference String reference, @JsFunctionName String functionName,
			@JsFunctionArguments Object... args);

	@JavaScriptFunction
	public JQueryElement find(GlobalReferenceHolder ref, String selector);

	@JavaScriptFunction
	public JQueryElement find(GlobalReferenceHolder ref, WebElement element);

	@JavaScriptFunction
	public JQueryElement find(GlobalReferenceHolder ref, List<WebElement> element);

	@JavaScriptFunction
	public JQueryElement find(GlobalReferenceHolder ref, String selector, WebDriver webDriver);

	@JavaScriptFunction
	public JQueryElement find(GlobalReferenceHolder ref, WebElement element, WebDriver webDriver);

	@JavaScriptFunction
	public List<WebElement> get(JQueryElement jQueryElement, WebDriver webDriver);

	@JavaScriptFunction
	public WebElement get(JQueryElement jQueryElement, int index, WebDriver webDriver);

	@JavaScriptFunction
	public void click(GlobalReferenceHolder ref, WebDriver webDriver);

	@JavaScriptFunction
	public void submit(GlobalReferenceHolder ref, WebDriver webDriver);

	@JavaScriptFunction
	public boolean is(GlobalReferenceHolder ref, String expression, WebDriver webDriver);

	@JavaScriptFunction
	public String prop(GlobalReferenceHolder ref, String name, WebDriver webDriver);

	@JavaScriptFunction
	public String attr(GlobalReferenceHolder ref, String name, WebDriver webDriver);

	@JavaScriptFunction
	public String text(GlobalReferenceHolder ref, WebDriver webDriver);

	@JavaScriptFunction
	public String val(GlobalReferenceHolder ref, WebDriver webDriver);

	@JavaScriptFunction
	public String val(GlobalReferenceHolder ref, String value, WebDriver webDriver);

	@JavaScriptFunction
	public String css(GlobalReferenceHolder ref, String name, WebDriver webDriver);

	@JavaScriptFunction
	public double width(GlobalReferenceHolder ref, WebDriver webDriver);

	@JavaScriptFunction
	public double height(GlobalReferenceHolder ref, WebDriver webDriver);

	@JavaScriptFunction
	public JQueryElement prev(GlobalReferenceHolder ref, String selector, WebDriver webDriver);

	/**
	 * Given a jQuery object that represents a set of DOM elements, the
	 * {@code .last()} method constructs a new jQuery object from the last
	 * element in that set.
	 * 
	 * @return returns last matching object wrapped by {@link JQueryElement} or
	 *         {@code NULL} when no matching elements are available
	 */
	@JavaScriptFunction
	public JQueryElement last(GlobalReferenceHolder ref, WebDriver webDriver);

	/**
	 * Given a jQuery object that represents a set of DOM elements, the
	 * {@code .first()} method constructs a new jQuery object from the first
	 * element in that set.
	 * 
	 * @return returns first matching object wrapped by {@link JQueryElement} or
	 *         {@code NULL} when no matching elements are available
	 */
	@JavaScriptFunction
	public JQueryElement first(GlobalReferenceHolder ref, WebDriver webDriver);

}
