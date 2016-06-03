package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation declares an ajax wait on class-level, which is executed
 * before each access on field causing locating. If you use the cache feature,
 * it will only be executed at first access.
 * <p>
 * Ajax-wait means, that all jquery ajax-calls must be finished.
 * </p>
 * 
 * @author Thomas
 *
 */
@Target({ ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface AjaxWait {
	/** max time in milliseconds (Default: 5 seconds) */
	int value() default 5000;
}
