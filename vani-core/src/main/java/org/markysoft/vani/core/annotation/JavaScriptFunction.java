package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a js-interface method as js-function. So the method
 * will be bound to the js-script source. You can declare the js-source as
 * expression in the annotation or reference a function by providing its name.
 * 
 * @author Thomas
 *
 */
@Target({ ElementType.METHOD })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JavaScriptFunction {
	/**
	 * Name of javaScript function, which should be executed when annotated
	 * method is called.
	 */
	String name() default "";

	/**
	 * JavaScript code, which should be executed when annotated method is
	 * called. <i>If you provide it, </i>{@code name}<i> value will be
	 * ignored.</i>
	 */
	String value() default "";
}
