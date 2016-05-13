package org.vani.core.locating.locator;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.vani.core.VaniContext;
import org.vani.core.javascript.JQuery;
import org.vani.core.locating.JQueryElement;

/**
 * This locator will use jQuery to locate {@link WebElement}.
 * 
 * @author Thomas
 *
 */
public class ByJQuery extends By {
	private String selector;
	private VaniContext vaniContext;
	private JQuery jquery;

	public ByJQuery(String selector, VaniContext vaniContext) {
		this.selector = selector;
		this.vaniContext = vaniContext;
		this.jquery = this.vaniContext.getAppContext().getBean(JQuery.class);
	}

	@Override
	public WebElement findElement(SearchContext context) {
		if (context instanceof WebElement) {
			WebDriver driver = null;
			if (context instanceof JQueryElement) {
				driver = ((JQueryElement) context).getWebDriver();
				return ((JQueryElement) context).find(selector);
			} else {
				driver = ((RemoteWebElement) context).getWrappedDriver();
				return jquery.find(null, (WebElement) context, driver).find(selector);
			}
		} else if (context instanceof WebDriver) {
			return find((WebDriver) context);
		} else {
			throw new IllegalArgumentException(
					"Provided search context '" + context + "' is not supported by jquery locating!");
		}
	}

	@Override
	public List<WebElement> findElements(SearchContext context) {
		if (context instanceof WebElement) {
			return jquery.find(null, (WebElement) context, ((RemoteWebElement) context).getWrappedDriver())
					.find(selector).get();
		} else if (context instanceof WebDriver) {
			return find((WebDriver) context).get();
		} else {
			throw new IllegalArgumentException(
					"Provided search context '" + context + "' is not supported by jquery locating!");
		}
	}

	public JQueryElement find() {
		return jquery.find(null, selector);
	}

	public JQueryElement find(WebDriver webDriver) {
		return jquery.find(null, selector, webDriver);
	}

	@Override
	public String toString() {
		return "By.jquery: " + selector;
	}
}
