package org.markysoft.vani.core.locating.locator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.markysoft.vani.core.annotation.LocatorBuilderClass;
import org.markysoft.vani.core.locating.RegionObject;

/**
 * Used to mark a field on a {@link RegionObject} to indicate locating by
 * JQuery.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@LocatorBuilderClass(JQueryLocatorBuilder.class)
public @interface FindByJQuery {
	/** JQuery selector */
	String value();
}
