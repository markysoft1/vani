package org.vani.core.locating.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vani.core.VaniContext;
import org.vani.core.annotation.ManualJavaScriptInterface;
import org.vani.core.javascript.JavaScriptException;
import org.vani.core.javascript.JavaScriptSource;
import org.vani.core.util.JavaScriptMethodInterceptor;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class JavaScriptProxyFactory {
	private final Log logger = LogFactory.getLog(getClass());
	@Autowired
	protected VaniContext vaniContext;

	public JavaScriptProxyFactory() {
	}

	public <T> T createProxy(JavaScriptSource<T> jsSource) {
		Class<T> jsInterface = jsSource.getJsInterface();
		T result = null;
		try {
			List<Object> manualJSInterfaceImplementations = loadManualJSInterfaceImplementation(jsInterface);
			//@formatter:off
			result = new ByteBuddy()
					.subclass(jsInterface)
					.method(ElementMatchers.any())
					.intercept(MethodDelegation.to(new JavaScriptMethodInterceptor(jsSource,vaniContext,manualJSInterfaceImplementations)))
					.make()
					.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded()
					.newInstance();
			//@formatter:on
		} catch (Exception ex) {
			logger.warn("cannot create proxy for JavaScript interface '" + jsInterface + "': " + ex.getMessage(), ex);
		}
		return result;
	}

	/**
	 * This method will collect all manual js-interface implementations for
	 * provided js-interface.
	 * <p>
	 * It will check all super interfaces, which are annotated with
	 * {@link ManualJavaScriptInterface} and instantiates its implementation.
	 * </p>
	 * 
	 * @param jsInterface
	 * @return returns a list of instances implementing manual interfaces for
	 *         provided {@code jsInterface}.
	 */
	protected List<Object> loadManualJSInterfaceImplementation(Class<?> jsInterface) {
		List<Object> result = new ArrayList<>(4);
		List<Class<?>> superInterfaces = new ArrayList<>(24);

		superInterfaces.addAll(Arrays.asList(jsInterface.getInterfaces()));
		while (!superInterfaces.isEmpty()) {
			Class<?> superInterface = superInterfaces.remove(0);
			ManualJavaScriptInterface manualJSAnnotation = superInterface
					.getDeclaredAnnotation(ManualJavaScriptInterface.class);
			if (manualJSAnnotation != null) {
				Set<?> manualImplementations = vaniContext.getReflections().getSubTypesOf(superInterface);
				if (!manualImplementations.isEmpty()) {
					if (manualImplementations.size() > 1) {
						throw new JavaScriptException(
								"Cannot determine which manual implementation should be used for '" + superInterface
										+ "'!");
					} else {
						Object instance = vaniContext.createBean((Class<?>) manualImplementations.iterator().next());
						result.add(instance);
					}
				}
			}
			superInterfaces.addAll(Arrays.asList(superInterface.getInterfaces()));
		}
		return result;
	}
}
