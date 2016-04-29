package org.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This tells {@code vani} to use marked method parameter for calling js
 * function name. This is only relevant in conjunction with custom call
 * functions.
 * <p>
 * The following example, shows its usage. If vani calls the corresponding
 * custom call function, it will provide the calling js function as second
 * parameter:
 * </p>
 * 
 * <pre>
 * <code>
 * &#64;JsCallFunction("classpath:vani-jquery-call.js")
 * public String call(@GlobalReference String reference, @JsFunctionName String functionName,
 * 	&#64;JsFunctionArguments Object... args);
 * </code>
 * </pre>
 * 
 * @author Thomas
 * @see JsFunctionArguments
 * @see JsCallFunction
 */
@Target({ ElementType.PARAMETER })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JsFunctionName {
}
