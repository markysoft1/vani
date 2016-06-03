package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.markysoft.vani.core.locating.LocatorBuilder;

@Target({ ElementType.ANNOTATION_TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface LocatorBuilderClass {
	Class<? extends LocatorBuilder> value();
}
