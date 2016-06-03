package org.markysoft.vani.core.locating;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.AjaxWait;
import org.markysoft.vani.core.annotation.ContentWait;
import org.markysoft.vani.core.util.FieldTypeInfo;
import org.markysoft.vani.core.util.VaniReflectionUtil;
import org.markysoft.vani.core.wait.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public abstract class BaseElementLocator<T> implements VaniElementLocator<T> {
	protected final Log logger = LogFactory.getLog(getClass());
	protected final SearchContext searchContext;
	protected final boolean shouldCache;
	protected final By by;
	protected T cachedElement;
	protected List<T> cachedElementList;
	protected FieldTypeInfo fieldTypeInfo;
	protected VaniContext vaniContext;
	protected WaitUtil waitUtil;
	protected VaniReflectionUtil reflectionUtil;

	/**
	 * Creates a new element locator.
	 * 
	 * @param searchContext
	 *            The context to use when finding the element
	 * @param by
	 * @param shouldCache
	 */
	public BaseElementLocator(SearchContext searchContext, By by, boolean shouldCache, FieldTypeInfo fieldTypeInfo,
			VaniContext vaniContext) {
		this.searchContext = searchContext;
		this.shouldCache = shouldCache;
		this.by = by;
		this.fieldTypeInfo = fieldTypeInfo;
		this.vaniContext = vaniContext;
		this.waitUtil = vaniContext.getAppContext().getBean(WaitUtil.class);
	}

	@Override
	public abstract T findElement();

	@Override
	public abstract List<T> findElements();

	/**
	 * This method will execute configured content waits.
	 * 
	 * @see ContentWait
	 * @see AjaxWait
	 */
	protected void executeContentWait() {
		ContentWait contentWait = fieldTypeInfo.getContentWait();
		if (contentWait != null) {
			String selector = vaniContext.resolveExpression(contentWait.value());
			String condition = vaniContext.resolveExpression(contentWait.condition());
			waitUtil.element(selector, getRootElement()).spel(condition).until(contentWait.timeout(),
					contentWait.pollingTime(), getWebDriver());
		}
		AjaxWait ajaxWait = fieldTypeInfo.getAjaxWait();
		if (ajaxWait != null) {
			waitUtil.ajaxJQuery(ajaxWait.value(), getWebDriver());
		}
	}

	protected SearchContext getRootElement() {
		return searchContext;
	}

	/**
	 * This method tries to find the corresponding {@link WebDriver} instance.
	 * <br/>
	 * <p>
	 * If {@code searchContext} is a {@link WebDriver} instance, it will be
	 * returned. If not it will check whether {@code searchContext} is an
	 * instance of {@link RemoteWebElement} and use
	 * {@link RemoteWebElement#getWrappedDriver()} to get it.
	 * </p>
	 * 
	 * @return returns corresponding {@link WebDriver} instance if availabel or
	 *         {@code NULL}
	 */
	protected WebDriver getWebDriver() {
		if (WebDriver.class.isAssignableFrom(searchContext.getClass())) {
			return (WebDriver) searchContext;
		} else if (RemoteWebElement.class.isAssignableFrom(searchContext.getClass())) {
			return ((RemoteWebElement) searchContext).getWrappedDriver();
		}
		return null;
	}

	/**
	 * This method will check whether {@code invalidated}-flag is set on
	 * corresponding bean instance.
	 * 
	 * @return returns true if underlying bean instance is marked as
	 *         {@code invalidated} or false if bean instance is not a
	 *         {@link RegionObject} or marked as {@code invalidated}
	 */
	protected boolean mustRelocateCachedElements() {
		boolean result = false;
		if (fieldTypeInfo.getBean() instanceof RegionObject) {
			result = ((RegionObject) fieldTypeInfo.getBean()).isInvalidated();
		}

		return result;
	}
}
