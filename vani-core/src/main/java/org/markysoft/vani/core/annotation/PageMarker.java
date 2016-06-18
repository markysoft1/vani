package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * After the annotated method is executed, {@code vani} checks whether the page,
 * currently displayed, has a page marker. This page marker tells {@code vani}
 * that the page is ready to continue the test.
 * 
 * @author Thomas
 *
 */
@Target({ ElementType.METHOD })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface PageMarker {
	/** name of your page marker */
	String value();

	/** max time to wait for request in milliseconds (default: 30 seconds) */
	long timeoutInMillis() default 30000;

	/**
	 * if true, wait will be skipped if annotated method returns {@code false},
	 * {@code NULL}, {@code 0} or {@code empty literal} (default: false)
	 */
	boolean disabledByReturn() default false;
}
