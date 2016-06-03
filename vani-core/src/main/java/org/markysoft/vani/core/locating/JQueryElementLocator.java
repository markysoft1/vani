package org.markysoft.vani.core.locating;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.locating.locator.ByJQuery;
import org.markysoft.vani.core.util.FieldTypeInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.By.ByCssSelector;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.By.ByName;
import org.openqa.selenium.By.ByTagName;
import org.openqa.selenium.SearchContext;
import org.springframework.util.ReflectionUtils;

public class JQueryElementLocator extends BaseElementLocator<JQueryElement> {

	public JQueryElementLocator(SearchContext searchContext, By by, boolean shouldCache, FieldTypeInfo fieldTypeInfo,
			VaniContext vaniContext) {
		super(searchContext, by, shouldCache, fieldTypeInfo, vaniContext);
	}

	@Override
	public JQueryElement findElement() {
		if (cachedElement != null && shouldCache && !mustRelocateCachedElements()) {
			return cachedElement;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("=========> getting jquery element BY\n\t'" + by + "' ON searchContext " + searchContext);
		}
		executeContentWait();
		JQueryElement result = null;
		if (by instanceof ByJQuery) {
			result = (JQueryElement) by.findElement(searchContext);
		} else {
			result = ((JQueryElement) searchContext).find(resolveSelector());
		}
		if (shouldCache) {
			cachedElement = result;
		}

		return result;
	}

	@Override
	public List<JQueryElement> findElements() {
		if (cachedElementList != null && !mustRelocateCachedElements()) {
			return cachedElementList;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("=========> getting jquery elements BY\n\t'" + by + "' ON searchContext " + searchContext);
		}
		executeContentWait();
		List<JQueryElement> elements = new ArrayList<>();
		if (by instanceof ByJQuery) {
			elements = (List) by.findElements(searchContext);
		} else {
			elements = Arrays.asList(((JQueryElement) searchContext).find(resolveSelector()));
		}
		cachedElementList = elements;

		return elements;
	}

	protected String resolveSelector() {
		if (by instanceof ById) {
			return "#" + getSelectorField("id");
		} else if (by instanceof ByClassName) {
			return "." + getSelectorField("className");
		} else if (by instanceof ByCssSelector) {
			return getSelectorField("selector");
		} else if (by instanceof ByTagName) {
			return getSelectorField("name");
		} else if (by instanceof ByName) {
			return getSelectorField("name");
		} else {
			throw new UnableToLocateException(
					"Unsupported selector in conjunction with jQuery element: " + by.getClass());
		}
	}

	protected String getSelectorField(String name) {
		try {
			Field field = by.getClass().getDeclaredField(name);
			ReflectionUtils.makeAccessible(field);
			return (String) field.get(by);
		} catch (Exception ex) {
			throw new UnableToLocateException("Cannot resolve selector of " + by.getClass()
					+ " for converting selenium-selector to jquery one: " + ex.getMessage(), ex);
		}
	}
}
