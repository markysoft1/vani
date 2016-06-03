package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * declares a script, which is used to check, whether javaScript source of
 * js-interface must be injected into page. For example:
 * 
 * <pre>
 * <code>
 * &#64;JavaScript(source = "classpath:jquery-2.2.1.js")
 * public interface JQuery {
 *	&#64;DetectionScript("typeof jQuery !== 'undefined'")
 *	public boolean isAvailable();
 * }
 * </code>
 * </pre>
 * <p>
 * For each call of method of associated js-interface, the detection script will
 * be executed.
 * </p>
 * 
 * @author Thomas
 *
 */
@Target({ ElementType.METHOD })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DetectionScript {
	/**
	 * JavaScript code, which should be executed to detect whether script must
	 * be injected
	 */
	String value();

	/**
	 * defines whether script specified by {@code value} should be wrapped by
	 * {code return}-statement. Default value is true.
	 */
	boolean autoReturn() default true;
}
