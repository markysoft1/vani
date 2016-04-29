package org.vani.core.locating.locator;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.vani.core.VaniContext;
import org.vani.core.locating.JQueryElement;
import org.vani.core.locating.LocatorBuilder;

/**
 * This class will be used to transfer {@link FindByJQuery} annotation to
 * {@link ByJQuery}. For that, it is also responsible for resolving spring
 * expression.
 * 
 * @author Thomas
 *
 */
public class JQueryLocatorBuilder implements LocatorBuilder<FindByJQuery, ByJQuery> {
	@Autowired
	private VaniContext vaniContext;

	@Override
	public ByJQuery build(FindByJQuery annotation) {
		return new ByJQuery(vaniContext.resolveExpression(annotation.value()), vaniContext);
	}

	public JQueryElement find(String selector) {
		return find(selector, null);
	}

	public JQueryElement find(String selector, WebDriver webDriver) {
		JQueryElement result = null;
		ByJQuery by = new ByJQuery(selector, vaniContext);
		if (webDriver != null) {
			result = by.find(webDriver);
		} else {
			result = by.find();
		}
		return result;
	}
}
