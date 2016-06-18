package org.markysoft.vani.core.util;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.PageMarker;
import org.markysoft.vani.core.annotation.Xhr;
import org.markysoft.vani.core.locating.PageMarkerHandler;
import org.openqa.selenium.WebDriver;
import org.springframework.util.StringUtils;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * This interceptor is responsible executing automatically waits for new page
 * marker. For more information, please see {@link PageMarker}.
 * 
 * @author Thomas
 *
 * @see Xhr
 */
public class PageMarkerInterceptor {
	protected VaniContext vaniContext;
	protected WebDriver webDriver;

	protected PageMarkerHandler pageMarkerHandler;

	public PageMarkerInterceptor(VaniContext vaniContext, WebDriver webDriver) {
		this.vaniContext = vaniContext;
		this.webDriver = webDriver;
		pageMarkerHandler = vaniContext.getAppContext().getBean(PageMarkerHandler.class);
	}

	@RuntimeType
	public Object intercept(@Origin Method invokedMethod, @SuperCall Callable<Object> zuper) throws Throwable {
		Object result = null;

		PageMarker pageMarkerAnno = invokedMethod.getAnnotation(PageMarker.class);
		String markerName = null;
		if (!StringUtils.isEmpty(pageMarkerAnno.value())) {
			markerName = vaniContext.resolveExpression(pageMarkerAnno.value());
		}
		pageMarkerHandler.setVaniMarker(webDriver);

		result = zuper.call();

		boolean skipWait = false;
		if (pageMarkerAnno.disabledByReturn()) {
			skipWait = result == null || (result instanceof Boolean && Boolean.FALSE.equals(result))
					|| (result instanceof String && StringUtils.isEmpty(result))
					|| (result instanceof Number && ((Number) result).longValue() == 0L);
		}
		if (!skipWait) {
			long timeoutInMillis = pageMarkerAnno.timeoutInMillis();
			pageMarkerHandler.waitUntilMarkerIsPresent(markerName, timeoutInMillis, webDriver);
		}
		return result;
	}
}
