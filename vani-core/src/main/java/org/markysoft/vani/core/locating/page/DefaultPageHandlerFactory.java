package org.markysoft.vani.core.locating.page;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.UrlMapping;
import org.markysoft.vani.core.util.VaniReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This implementation wraps the desired page handler with
 * {@link DefaultPageHandler} and sets the method map (pattern => method).
 * 
 * @author Thomas
 *
 */
public class DefaultPageHandlerFactory implements PageHandlerFactory {
	protected Log logger = LogFactory.getLog(getClass());
	@Autowired
	protected VaniContext vaniContext;
	@Autowired
	protected VaniReflectionUtil reflectionUtil;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> PageHandler<T> create(Class<T> handlerClass) {
		T target = vaniContext.createBean(handlerClass);
		PageHandler<T> result = new DefaultPageHandler(target, vaniContext);
		Map<Pattern, Method> methodMap = getMethodMap(handlerClass);
		((DefaultPageHandler) result).setMethodMap(methodMap);
		return result;
	}

	/**
	 * This method collects all methods annotated with {@link UrlMapping} and
	 * creates a map between pattern and its method. It also include the class
	 * level annotation as prefix for all method level one.
	 * 
	 * @param handlerClass
	 * @return returns a map with pattern as key and corresponding method as
	 *         value
	 */
	protected <T> Map<Pattern, Method> getMethodMap(Class<T> handlerClass) {
		Map<Pattern, Method> result = new HashMap<>();
		String urlPrefix = "";
		UrlMapping urlMapping = handlerClass.getAnnotation(UrlMapping.class);
		if (urlMapping != null) {
			urlPrefix = urlMapping.value();
		}
		List<Method> methods = reflectionUtil.getAnnotatedMethodsWith(handlerClass, UrlMapping.class, null);
		for (Method method : methods) {
			urlMapping = method.getAnnotation(UrlMapping.class);
			String url = urlPrefix + urlMapping.value();
			url = vaniContext.resolveExpression(url);
			Pattern pattern = Pattern.compile(url);
			result.put(pattern, method);
			logger.debug("[" + url + "] bound to page handler '" + method + "'");
		}

		return result;
	}

}
