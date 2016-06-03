package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation declares the page url on class level or marks a method
 * delivering desired url.
 * <p>
 * If you provide an url in the {@link Page} annotation, this url will be
 * preferred.
 * </p>
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface PageUrl {
	String value() default "";
}
