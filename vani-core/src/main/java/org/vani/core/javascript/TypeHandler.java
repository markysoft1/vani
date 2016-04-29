package org.vani.core.javascript;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.vani.core.annotation.JsTypeHandler;
import org.vani.core.locating.JQueryElement;

/**
 * This class is responsible for converting the result of a javascript function
 * into another type.
 * 
 * <p>
 * If your js-interface defines a return type, which is not supported by
 * {@link JavascriptExecutor}, vani tries to find an {@link TypeHandler}, which
 * is able to convert to the declared return type.
 * </p>
 * <p>
 * For example, the jquery interface returns {@link JQueryElement}. Therefore
 * there is a implementation for converting to the required type.
 * </p>
 * <p>
 * <h3>Declaration and Registration</h3>You only have to implements this
 * interface and annotated the class with {@link JsTypeHandler}. That's all.
 * <br>
 * All type handlers will be instantiated and registered during spring context
 * startup.
 * </p>
 * 
 * @author Thomas
 *
 * @param <T>
 *            target type for converting
 * @param <S>
 *            type returned by javascript execution
 * 
 * @see {@link JsTypeHandler}
 */
public interface TypeHandler<T, S> {
	/**
	 * This method returns the target type of the handler. This means, that
	 * current handler is able to handle the returned type.
	 * 
	 * @return returns the target type of the handler.
	 */
	public Class<T> getTargetType();

	/**
	 * This method transfers the provided {@code scriptResult} to the target
	 * type of current handler.
	 * 
	 * @param scriptResult
	 * @param webDriver
	 * @return returns the converted value.
	 */
	public T get(S scriptResult, WebDriver webDriver);
}
