package org.markysoft.vani.core.locating.page;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.locating.PageObject;
import org.markysoft.vani.core.locating.RegionObject;
import org.markysoft.vani.core.locating.factory.RegionFactory;
import org.openqa.selenium.WebDriver;

/**
 * This implementation use a method map to find correct handler method. The map
 * contains the mapping between url patterns and its handler method.
 * 
 * @author Thomas
 *
 * @param <T>
 */
public class DefaultPageHandler<T> implements PageHandler<T> {
	protected Log logger = LogFactory.getLog(getClass());
	protected T handler;
	protected Map<Pattern, Method> methodMap;
	protected VaniContext vaniContext;
	protected RegionFactory regionFactory;

	public DefaultPageHandler(T handler, VaniContext vaniContext) {
		this.handler = handler;
		this.methodMap = new HashMap<>();
		this.vaniContext = vaniContext;
		this.regionFactory = vaniContext.getAppContext().getBean(RegionFactory.class);
	}

	/**
	 * This method will set the method mapping between url mappings and its
	 * handler methods.
	 * <p>
	 * If you provide {@code NULL} as parameter, current map will only be
	 * cleared
	 * </p>
	 * 
	 * @param methodMap
	 *            mapping between url pattern and handler method
	 */
	protected void setMethodMap(Map<Pattern, Method> methodMap) {
		if (methodMap == null) {
			this.methodMap.clear();
		} else {
			this.methodMap = methodMap;
		}
	}

	@Override
	public boolean isApplicable(String url) {
		boolean result = getApplicable(url) != null;
		return result;
	}

	/**
	 * This method looks for the best matching url pattern. It's based on the
	 * {@link Matcher#group()} and use the group with the max length.
	 * 
	 * @param url
	 * @return returns corresponding method of best matching url pattern or
	 *         {@code NULL} if no match could be found.
	 */
	protected Method getApplicable(String url) {
		Method result = null;
		int matching = 0;
		for (Pattern pattern : methodMap.keySet()) {
			Matcher m = pattern.matcher(url);
			if (m.find()) {
				String group = m.group();
				if (group.length() > matching) {
					result = methodMap.get(pattern);
					matching = group.length();
				}
			}
		}
		return result;
	}

	@Override
	public void handle(String url, WebDriver webDriver) {
		Method method = getApplicable(url);
		if (method != null) {
			List<Object> parameters = new ArrayList<>();
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes != null) {
				boolean urlAdded = false;
				for (Class<?> paramType : parameterTypes) {
					if (String.class.isAssignableFrom(paramType) && !urlAdded) {
						parameters.add(url);
						urlAdded = true;
					} else if (WebDriver.class.isAssignableFrom(paramType)) {
						parameters.add(webDriver);
					} else if (PageObject.class.isAssignableFrom(paramType)) {
						parameters.add(regionFactory.createPage(paramType, webDriver, url));
					} else if (RegionObject.class.isAssignableFrom(paramType)) {
						parameters.add(regionFactory.create(paramType, webDriver));
					} else {
						logger.debug("No parameter mapping found for type '" + paramType.getSimpleName()
								+ "' for handler method '" + method + "' of target handler '" + handler.getClass()
								+ "'!");
						parameters.add(null);
					}
				}
			}
			try {
				method.invoke(handler, parameters.toArray());
			} catch (InvocationTargetException ex) {
				if (ex.getCause() != null) {
					logger.error("Method '" + method + "' of PageHandler '" + handler.getClass()
							+ "' throws an exception: " + ex.getCause(), ex.getCause());
				} else {
					logger.error("Method '" + method + "' of PageHandler '" + handler.getClass()
							+ "' throws an exception: " + ex, ex);
				}
			} catch (Exception ex) {
				logger.error("Method '" + method + "' of PageHandler '" + handler.getClass() + "' throws an exception: "
						+ ex, ex);
			}
		}
	}

	@Override
	public Set<String> getUrlPatterns() {
		Set<String> result = new HashSet<>();
		for (Pattern pattern : methodMap.keySet()) {
			result.add(pattern.pattern());
		}
		return result;
	}
}
