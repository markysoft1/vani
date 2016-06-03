package org.markysoft.vani.core.locating;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.locating.factory.LocatorBuilderFactory;
import org.markysoft.vani.core.locating.locator.ByJQuery;
import org.markysoft.vani.core.locating.locator.JQueryLocatorBuilder;
import org.markysoft.vani.core.wait.WaitUtil;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * basic implementation of a region object.
 * 
 * @author Thomas
 *
 */
public abstract class RegionObject {
	protected WebDriver webDriver;
	@Autowired
	protected VaniContext vaniContext;
	protected JQueryLocatorBuilder $;
	@Autowired
	protected WaitUtil waitUtil;
	private boolean invalidated;

	@PostConstruct
	protected void initJQuery() {
		$ = (JQueryLocatorBuilder) vaniContext.getAppContext().getBean(LocatorBuilderFactory.class)
				.get(JQueryLocatorBuilder.class);
	}

	/**
	 * @return returns the underlying {@code webDriver} instance
	 */
	public WebDriver getWebDriver() {
		return webDriver;
	}

	/**
	 * This method will check whether current region source code contains
	 * provided expression (regex is also supported).
	 * 
	 * @param expr
	 * @return returns true if provided expression is found, else false will be
	 *         returned
	 */
	public boolean containsPageSource(String expr) {
		boolean result = false;
		if (!StringUtils.isEmpty(expr)) {
			String source = webDriver.getPageSource();
			Pattern regex = Pattern.compile(expr);
			result = regex.matcher(source).find();
		}
		return result;
	}

	/**
	 * This method will execute the provided {@code selector} with
	 * {@link ByJQuery} locator.
	 * 
	 * @param selector
	 * @return
	 */
	protected JQueryElement $(String selector) {
		return $.find(selector, webDriver);
	}

	/**
	 * marks current object as invalidate. This means that all cached elements
	 * must relocated before next access on it. The relocating will be performed
	 * automatically.
	 */
	public void invalidate() {
		this.invalidated = true;
	}

	/**
	 * @return returns true if all cached elements of current object must be
	 *         relocated before next access on it.
	 */
	public boolean isInvalidated() {
		return this.invalidated;
	}
}
