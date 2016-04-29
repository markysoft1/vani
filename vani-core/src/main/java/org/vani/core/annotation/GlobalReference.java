package org.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.vani.core.javascript.GlobalReferenceHolder;
import org.vani.core.locating.JQueryElement;

/**
 * This tells {@code vani} to use marked method parameter for provided global
 * reference value. This is only relevant in conjunction with custom call
 * functions.
 * <p>
 * A global reference could be used, to implements a page caching mechanism like
 * jquery elements. If you work with {@link JQueryElement}, you don't get the
 * actual element object from {@code webDriver}, because it returns only a
 * reference string. This reference string will be wrapped by
 * {@link JQueryElement}. If you call a method of that object. the wrapped
 * reference string is provided as parameter and the call function will look for
 * stored element identified by that value.
 * </p>
 * <p>
 * The following example, shows its usage. If {@code vani} calls the
 * corresponding custom call function, it will provide the reference string
 * contained by specified {@link GlobalReferenceHolder} as first parameter:
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
 * @see JsFunctionName
 * @see JsCallFunction
 * @see GlobalReferenceHolder
 */
@Target({ ElementType.PARAMETER })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalReference {
}
