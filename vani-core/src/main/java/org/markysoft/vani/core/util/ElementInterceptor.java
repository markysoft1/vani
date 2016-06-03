package org.markysoft.vani.core.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.markysoft.vani.core.locating.RegionObject;
import org.markysoft.vani.core.locating.UnableToLocateException;
import org.markysoft.vani.core.locating.VaniElementLocator;
import org.openqa.selenium.WebElement;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * This interceptor is responsible for loading {@link WebElement} and if you
 * specified, it calls wrapping by {@link RegionObject}.
 * 
 * @author Thomas
 *
 */
public class ElementInterceptor {
	private VaniElementLocator<?> elementLocator;

	public ElementInterceptor(VaniElementLocator<?> elementLocator) {
		this.elementLocator = elementLocator;
	}

	private <T> T loadTarget(Class<T> targetClass) {
		T result = null;

		if (Map.class.isAssignableFrom(targetClass)) {
			// setMapValue(field, region, elements);
		} else if (List.class.isAssignableFrom(targetClass)) {
			result = (T) elementLocator.findElements();
		} else {
			result = (T) elementLocator.findElement();
		}

		return result;
	}

	@RuntimeType
	public Object intercept(@AllArguments Object[] arguments, @Origin Method invokedMethod,
			@Origin Class<?> targetClass) {

		Object result = null;

		if (Object.class.equals(targetClass)) {
			targetClass = invokedMethod.getDeclaringClass();
		}
		Object targetObj = loadTarget(targetClass);

		try {
			result = invokedMethod.invoke(targetObj, arguments);
		} catch (InvocationTargetException ex) {
			Throwable throwable = ex;
			if (ex.getCause() != null) {
				throwable = ex.getCause();
			}
			throw new UnableToLocateException(
					"cannot invoke method (" + invokedMethod + ") of target object (" + targetClass + "): " + throwable,
					throwable);
		} catch (Exception ex) {
			throw new UnableToLocateException(
					"cannot invoke method (" + invokedMethod + ") of target object (" + targetClass + "): ", ex);
		}

		return result;
	}
}
