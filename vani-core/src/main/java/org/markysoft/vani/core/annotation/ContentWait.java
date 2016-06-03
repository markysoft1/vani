package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation declare a content wait on class-level, which is executed
 * before each access on field causing locating. If you use the cache feature,
 * it will only be executed at first access.
 * 
 * @author Thomas
 *
 */
@Target({ ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentWait {
	/** JQuery selector */
	String value();

	/**
	 * wait condition defined with spring expression language (Default:
	 * hasMatches()
	 */
	String condition() default "hasMatches()";

	/** max time to wait in milliseconds (Default: 10 seconds) */
	int timeout() default 10000;

	/**
	 * time to wait between each checking whether condition is true in
	 * milliseconds (Default: 1 second)
	 */
	int pollingTime() default 1000;
}
