package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a method as method as call function source provider. A
 * call function will be used to wrap the bound js-function call. If you don't
 * provide one, the default one will be used. For example of the js-source with
 * default call source:
 * 
 * <pre>
 * <code> return showMessage.apply(null,arguments);
 * </code>
 * </pre>
 * <p>
 * To provide your own source, you must declare following:
 * </p>
 * 
 * <pre>
 * <code>
 * &#64;JsCallFunction("classpath:vani-jquery-call.js")
 * public String call(@JsFunctionName String functionName,&#64;JsFunctionArguments Object... args);
 * </code>
 * </pre>
 * 
 * @author Thomas
 * @see JsFunctionArguments
 * @see JsFunctionName
 */
@Target({ ElementType.METHOD })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JsCallFunction {

	String value();
}
