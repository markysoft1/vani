package org.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * After the annotated method is executed, {@code vani} checks whether a
 * xhr-request made by jquery for provided url is finished. For that, XHR
 * tracking must be enabled by injecting jquery javascript (done during first
 * jquery locating), because {@code vani} will register a listener and track all
 * xhr-requests in a page cache.
 * 
 * @author Thomas
 *
 */
@Target({ ElementType.METHOD })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Xhr {
	String value();

	/** max time to wait for request in milliseconds (default: 30 seconds) */
	long timeoutInMillis() default 30000;

	/**
	 * if true, wait will be skipped if annotated method returns {@code false},
	 * {@code NULL}, {@code 0} or {@code empty literal} (default: false)
	 */
	boolean disabledByReturn() default false;
}
