package org.vani.core.locating;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.vani.core.VaniContext;
import org.vani.core.locating.factory.RegionFactory;
import org.vani.core.util.FieldTypeInfo;

public class RegionElementLocator extends BaseElementLocator<RegionObject> {
	protected RegionFactory regionFactory;

	public RegionElementLocator(SearchContext searchContext, By by, boolean shouldCache, FieldTypeInfo fieldTypeInfo,
			RegionFactory regionFactory, VaniContext vaniContext) {
		super(searchContext, by, shouldCache, fieldTypeInfo, vaniContext);
		this.regionFactory = regionFactory;
	}

	@Override
	public RegionObject findElement() {
		if (cachedElement != null && shouldCache && !mustRelocateCachedElements()) {
			return cachedElement;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("=========> getting region element BY\n\t'" + by + "' ON searchContext " + searchContext);
		}
		executeContentWait();
		WebElement element = searchContext.findElement(by);
		RegionObject result = resolve(element);
		if (shouldCache) {
			cachedElement = result;
		}

		return result;
	}

	@Override
	public List<RegionObject> findElements() {
		if (cachedElementList != null && !mustRelocateCachedElements()) {
			return cachedElementList;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("=========> getting region elements BY\n\t'" + by + "' ON searchContext " + searchContext);
		}
		executeContentWait();
		List<WebElement> elements = searchContext.findElements(by);
		List<RegionObject> result = resolve(elements);
		cachedElementList = result;

		return result;
	}

	/**
	 * This method wraps specified root element with corresponding
	 * {@link RegionObject} implementation
	 * 
	 * @param element
	 *            root element or {@code NULL} if no root is available
	 * @return returns {@link RegionObject} specified by field type
	 */
	protected <T> T resolve(WebElement element) {
		Class<T> type = fieldTypeInfo.getFieldType();
		T result = regionFactory.create(type, fieldTypeInfo.getWebDriver(), element);
		return result;
	}

	/**
	 * This method creates for each provided {@link WebElement} an appropriate
	 * {@link RegionObject}. The type of {@link RegionObject} depends on generic
	 * type of given list.
	 * <p>
	 * If current field doesn't declare a generic type,
	 * {@code NULL will be returned}. </p<
	 * 
	 * @param elements
	 * @return returns a list of {@link RegionObject} per provided element or
	 *         {@code NULL} will be returned if current field has no generic
	 *         type.
	 */
	protected <T> List<T> resolve(List<WebElement> elements) {
		Class<T> type = fieldTypeInfo.getFirstGenericType();
		if (type == null) {
			logger.warn("cannot determine target region type of list without generic type: " + fieldTypeInfo.getField()
					+ " of " + fieldTypeInfo.getClass());
			return null;
		}
		List<T> result = new ArrayList<T>(elements.size());
		for (WebElement element : elements) {
			T obj = regionFactory.create(type, element);
			if (obj != null) {
				result.add(obj);
			}
		}
		return result;
	}

}
