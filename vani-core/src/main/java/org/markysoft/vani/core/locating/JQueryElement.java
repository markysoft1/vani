package org.markysoft.vani.core.locating;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.markysoft.vani.core.javascript.GlobalReferenceHolder;
import org.markysoft.vani.core.javascript.JQuery;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class JQueryElement implements WebElement, GlobalReferenceHolder {
	private JQuery jquery;
	private WebDriver webDriver;
	private String reference;
	private String selector;
	private long length;

	public JQueryElement() {
	}

	public JQueryElement(JQuery jquery, WebDriver webDriver, String ref) {
		this.jquery = jquery;
		this.webDriver = webDriver;
		this.reference = ref;

		this.selector = jquery.objectAttribute(this, "selector", webDriver);
		this.length = jquery.objectAttribute(this, "length", webDriver);
	}

	public JQueryElement(JQuery jquery, WebDriver webDriver, WebElement webElement) {
		this.jquery = jquery;
		this.webDriver = webDriver;

		this.reference = jquery.find(null, webElement).reference;

		this.selector = jquery.objectAttribute(this, "selector", webDriver);
		this.length = jquery.objectAttribute(this, "length", webDriver);
	}

	public JQueryElement(JQuery jquery, WebDriver webDriver, List<WebElement> webElements) {
		this.jquery = jquery;
		this.webDriver = webDriver;

		this.reference = jquery.find(null, webElements).reference;

		this.selector = jquery.objectAttribute(this, "selector", webDriver);
		this.length = jquery.objectAttribute(this, "length", webDriver);
	}

	/**
	 * Capture the screenshot of <b>FIRST</b> wrapped element and store it in
	 * the specified location.
	 *
	 * <p>
	 * For WebDriver extending TakesScreenshot, this makes a best effort
	 * depending on the browser to return the following in order of preference:
	 * <ul>
	 * <li>Entire page</li>
	 * <li>Current window</li>
	 * <li>Visible portion of the current frame</li>
	 * <li>The screenshot of the entire display containing the browser</li>
	 * </ul>
	 *
	 * <p>
	 * For WebElement extending TakesScreenshot, this makes a best effort
	 * depending on the browser to return the following in order of preference:
	 * - The entire content of the HTML element - The visisble portion of the
	 * HTML element
	 *
	 * @param <X>
	 *            Return type for getScreenshotAs.
	 * @param target
	 *            target type, @see OutputType
	 * @return Object in which is stored information about the screenshot <b>OR
	 *         {@code NULL} if there are no wrapped elements</b>
	 * @throws WebDriverException
	 *             on failure.
	 */
	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		WebElement first = get(0);
		return first != null ? first.getScreenshotAs(target) : null;
	}

	@Override
	public void click() {
		get(0).click(); // necessary to avoid problem with clicking normal a
						// tags
	}

	@Override
	public void submit() {
		jquery.submit(this, webDriver);
	}

	public long getLength() {
		return length;
	}

	public String getSelector() {
		return selector;
	}

	/**
	 * This method will use the {@link JQueryElement#val(String)}-method to set
	 * the value of a text input.
	 */
	@Override
	public void sendKeys(CharSequence... keysToSend) {
		StringBuilder builder = new StringBuilder();

		if (keysToSend != null) {
			for (CharSequence keys : keysToSend) {
				builder.append(keys);
			}
		}
		val(builder.toString());
	}

	@Override
	public void clear() {
		val("");
	}

	@Override
	public String getTagName() {
		String tagName = prop("tagName");
		return tagName;
	}

	@Override
	public String getAttribute(String name) {
		String result = attr(name);
		return result;
	}

	@Override
	public boolean isSelected() {
		boolean result = is(":selected") || is(":checked");
		return result;
	}

	@Override
	public boolean isEnabled() {
		boolean result = is(":enabled");
		return result;
	}

	@Override
	public String getText() {
		return text();
	}

	@Override
	public List<WebElement> findElements(By by) {
		List<WebElement> wrappedElements = get();
		List<WebElement> result = new ArrayList<>(30);
		wrappedElements.forEach(e -> result.add(new JQueryElement(jquery, webDriver, e.findElements(by))));
		return result;
	}

	@Override
	public WebElement findElement(By by) {
		List<WebElement> wrappedElements = get();
		List<WebElement> result = new ArrayList<>(30);
		wrappedElements.forEach(e -> result.addAll(e.findElements(by)));
		return new JQueryElement(jquery, webDriver, result);
	}

	@Override
	public boolean isDisplayed() {
		boolean result = is(":visible");
		return result;
	}

	@Override
	public Point getLocation() {
		Point result = offset();
		return result;
	}

	@Override
	public Dimension getSize() {
		Dimension result = new Dimension((int) width(), (int) height());
		return result;
	}

	@Override
	public Rectangle getRect() {
		Rectangle result = new Rectangle(getLocation(), getSize());
		return result;
	}

	@Override
	public String getCssValue(String propertyName) {
		String result = css(propertyName);
		return result;
	}

	public List<WebElement> get() {
		return jquery.get(this, webDriver);
	}

	public WebElement get(int index) {
		return jquery.get(this, index, webDriver);
	}

	public boolean hasMatches() {
		boolean result = get(0) != null;
		return result;
	}

	/**
	 * Check the current matched set of elements against a selector
	 * 
	 * @param expr
	 * @return returns true if selector is applicable for all wrapped elements
	 *         of this jquery object or false if it is not so.
	 */
	public boolean is(String expr) {
		return jquery.is(this, expr, webDriver);
	}

	/**
	 * The .prop() method gets the property value for only the first element in
	 * the matched set. It returns {@code NULL} for the value of a property that
	 * has not been set, or if the matched set has no elements.
	 * 
	 * @param name
	 * @return returns value of specified property or {@code NULL} if not set or
	 *         this jquery object has no dom elements.
	 */
	public String prop(String name) {
		return jquery.prop(this, name, webDriver);
	}

	/**
	 * The .attr() method gets the attribute value for only the first element in
	 * the matched set. It returns {@code NULL} for the value of a attribute
	 * that has not been set, or if the matched set has no elements.
	 * 
	 * @param name
	 * @return returns value of specified attribute or {@code NULL} if not set
	 *         or this jquery object has no dom elements.
	 */
	public String attr(String name) {
		return jquery.attr(this, name, webDriver);
	}

	@Override
	public String getReference() {
		return reference;
	}

	public JQueryElement find(String selector) {
		JQueryElement result = jquery.find(this, selector, webDriver);
		return result;
	}

	/**
	 * Get the combined text contents of each element in the set of matched
	 * elements, including their descendants. <br>
	 * <br>
	 * Unlike the .html() method, .text() can be used in both XML and HTML
	 * documents. The result of the .text() method is a string containing the
	 * combined text of all matched elements. (Due to variations in the HTML
	 * parsers in different browsers, the text returned may vary in newlines and
	 * other white space.)
	 * 
	 * @return returns the combined text contents of each element in the set of
	 *         matched elements, including their descendants.
	 */
	public String text() {
		return jquery.text(this, webDriver);
	}

	/**
	 * method to get css value of specified property of first wrapped element.
	 * 
	 * @param propertyName
	 * @return returns css value of specified property or {@code NULL} if css
	 *         property is not set or no wrapped elements are available
	 */
	public String css(String propertyName) {
		return jquery.css(this, propertyName, webDriver);
	}

	/**
	 * set the value of every matched element.
	 * 
	 * @param value
	 *            new value
	 */
	public void val(String value) {
		jquery.val(this, value, webDriver);
	}

	/**
	 * Get the current value of the first element
	 * 
	 * @param value
	 *            new value
	 */
	public void val() {
		jquery.val(this, webDriver);
	}

	public double width() {
		return jquery.width(this, webDriver);
	}

	public double height() {
		return jquery.height(this, webDriver);
	}

	public Point offset() {
		return null; // TODO: check what selenium returns for offset()
	}

	public void each(Consumer<JQueryElement> consumer) {
		for (WebElement element : get()) {
			consumer.accept(jquery.find(null, element, webDriver));
		}
	}

	public JQueryElement prev(String selector) {
		return jquery.prev(this, selector, webDriver);
	}

	@Override
	public String toString() {
		return "JQuery[selector: " + selector + ", length: " + length + "]";
	}

	/**
	 * Given a jQuery object that represents a set of DOM elements, the
	 * {@code .last()} method constructs a new jQuery object from the last
	 * element in that set.
	 * 
	 * @return returns last matching object wrapped by {@link JQueryElement} or
	 *         {@code NULL} when no matching elements are available
	 */
	public JQueryElement last() {
		return jquery.last(this, webDriver);
	}

	/**
	 * Given a jQuery object that represents a set of DOM elements, the
	 * {@code .first()} method constructs a new jQuery object from the first
	 * element in that set.
	 * 
	 * @return returns first matching object wrapped by {@link JQueryElement} or
	 *         {@code NULL} when no matching elements are available
	 */
	public JQueryElement first() {
		return jquery.first(this, webDriver);
	}

	/**
	 * @return returns the associated webDriver instance.
	 */
	public WebDriver getWebDriver() {
		return webDriver;
	}
}
