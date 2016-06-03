package org.markysoft.vani.core.util;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.Xhr;
import org.markysoft.vani.core.wait.WaitUtil;
import org.openqa.selenium.WebDriver;
import org.springframework.util.StringUtils;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * This interceptor is responsible executing automatically waits for finishing
 * xhr-requests. For more information, please see {@link Xhr}.
 * 
 * @author Thomas
 *
 * @see Xhr
 */
public class XhrInterceptor {
	protected VaniContext vaniContext;
	protected WebDriver webDriver;
	protected WaitUtil waitUtil;

	public XhrInterceptor(VaniContext vaniContext, WebDriver webDriver) {
		this.vaniContext = vaniContext;
		this.webDriver = webDriver;
		this.waitUtil = vaniContext.getAppContext().getBean(WaitUtil.class);
	}

	@RuntimeType
	public Object intercept(@Origin Method invokedMethod, @SuperCall Callable<Object> zuper) throws Throwable {
		Object result = null;

		long start = System.currentTimeMillis();
		result = zuper.call();

		Xhr xhr = invokedMethod.getAnnotation(Xhr.class);
		boolean skipWait = false;
		if (xhr.disabledByReturn()) {
			skipWait = result == null || (result instanceof Boolean && Boolean.FALSE.equals(result))
					|| (result instanceof String && StringUtils.isEmpty(result))
					|| (result instanceof Number && ((Number) result).longValue() == 0L);
		}
		if (!skipWait) {
			String url = xhr.value();
			url = vaniContext.resolveExpression(url);

			long timeoutInMillis = xhr.timeoutInMillis();
			waitUtil.ajaxJQuery(url, start, timeoutInMillis, webDriver);
		}
		return result;
	}
}
