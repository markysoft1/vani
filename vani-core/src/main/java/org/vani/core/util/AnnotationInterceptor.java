package org.vani.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.openqa.selenium.support.FindBy;
import org.springframework.util.StringUtils;
import org.vani.core.VaniContext;
import org.vani.core.locating.UnableToLocateException;
import org.vani.core.locating.factory.AnnotationProxyFactory;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * This class is responsible for resolving spring's placeholders in attributes
 * of annotations like {@link FindBy}.
 * <p>
 * There are three ways of handling:
 * </p>
 * <ul>
 * <li><b>string array</b> each entry will be resolved</li>
 * <li><b>string value</b> value will be resolved</li>
 * <li><b>annotation</b> will be wrapped and new interceptor instance will
 * handle its attributes</li>
 * </ul>
 * 
 * @author Thomas
 *
 */
public class AnnotationInterceptor {
	private Annotation annotation;
	private VaniContext vaniContext;
	private AnnotationProxyFactory annotationProxyFactory;

	public AnnotationInterceptor(Annotation annotation, VaniContext vaniContext,
			AnnotationProxyFactory annotationProxyFactory) {
		this.vaniContext = vaniContext;
		this.annotation = annotation;
		this.annotationProxyFactory = annotationProxyFactory;
	}

	/**
	 * This method resolves placeholders contained by provided value. It
	 * supports string arrays, annotation (will be wrapped by proxy) and string
	 * value.
	 * 
	 * @param value
	 * @return returns resolved value or {@code NULL} when you passed it.
	 */
	protected Object resolvePlaceholders(Object value) {
		if (value != null) {
			if (value.getClass().isArray()) {
				int length = Array.getLength(value);
				Object[] result = new Object[length];
				for (int i = 0; i < length; i++) {
					result[i] = resolvePlaceholders(Array.get(value, i));
				}
				value = result;
			} else if (value instanceof Annotation) {
				value = annotationProxyFactory.createProxy((Annotation) value);
			} else if (value instanceof String) {
				String expression = value.toString();
				if (!StringUtils.isEmpty(expression)) {
					value = vaniContext.resolveExpression(expression);
				}
			}
		}

		return value;
	}

	@RuntimeType
	public Object intercept(@AllArguments Object[] arguments, @Origin Method invokedMethod,
			@Origin Class<?> targetClass) {

		Object result = null;

		try {
			result = invokedMethod.invoke(annotation, arguments);
		} catch (Exception ex) {
			throw new UnableToLocateException(
					"cannot invoke method (" + invokedMethod + ") of target annotation (" + targetClass + "): ", ex);
		}

		result = resolvePlaceholders(result);
		return result;
	}
}
