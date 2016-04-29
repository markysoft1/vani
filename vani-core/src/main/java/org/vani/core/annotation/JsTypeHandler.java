package org.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.vani.core.javascript.TypeHandler;

/**
 * This annotation marks a class as custom {@link TypeHandler} for converting
 * type between javascript execution and java js-interfaces.
 * <p>
 * You only have to mark {@link TypeHandler}-implementation with this annotation
 * and vani will do the rest (instantiating, dependency injection and spring
 * registration).
 * </p>
 * 
 * @author Thomas
 *
 */
@Target({ ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JsTypeHandler {
}
