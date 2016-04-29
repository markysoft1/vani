package org.vani.core.locating;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.vani.core.VaniContext;
import org.vani.core.util.FieldTypeInfo;

public class WebElementLocator extends BaseElementLocator<WebElement> {

	public WebElementLocator(SearchContext searchContext, By by, boolean shouldCache, FieldTypeInfo fieldTypeInfo,
			VaniContext vaniContext) {
		super(searchContext, by, shouldCache, fieldTypeInfo, vaniContext);
	}

	@Override
	public WebElement findElement() {
		if (cachedElement != null && shouldCache && !mustRelocateCachedElements()) {
			return cachedElement;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("=========> getting WebElement BY\n\t'" + by + "' ON searchContext " + searchContext);
		}
		executeContentWait();
		WebElement element = searchContext.findElement(by);
		if (shouldCache) {
			cachedElement = element;
		}

		return element;
	}

	@Override
	public List<WebElement> findElements() {
		if (cachedElementList != null && !mustRelocateCachedElements()) {
			return cachedElementList;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("=========> getting web elements BY\n\t'" + by + "' ON searchContext " + searchContext);
		}
		executeContentWait();
		List<WebElement> elements = searchContext.findElements(by);
		cachedElementList = elements;

		return elements;
	}

}
