package org.vani.core.locating.factory;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.vani.core.locating.VaniElementLocator;
import org.vani.core.util.FieldTypeInfo;

/**
 * A factory for producing {@link VaniElementLocator}s. It is expected that a
 * new ElementLocator will be returned per call. <br>
 * This is a copy of
 * {@link org.openqa.selenium.support.pagefactory.ElementLocatorFactory} with
 * one difference. The result has the type {@link VaniElementLocator} instead of
 * {@link ElementLocator}.
 * 
 */
public interface ElementLocatorFactory {
	/**
	 * When a field on a class needs to be decorated with an
	 * {@link VaniElementLocator} this method will be called.
	 * 
	 * @param by
	 * @param shouldCache
	 * @return an VaniElementLocator object.
	 */
	VaniElementLocator<?> createLocator(FieldTypeInfo fieldTypeInfo, SearchContext searchContext, By by,
			boolean shouldCache);
}
