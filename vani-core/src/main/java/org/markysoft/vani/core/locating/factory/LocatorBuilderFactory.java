package org.markysoft.vani.core.locating.factory;

import org.markysoft.vani.core.locating.LocatorBuilder;

/**
 * The implementations of this interface are responsible for creating new
 * instances for given {@link LocatorBuilder} types.
 * 
 * <p>
 * A {@link LocatorBuilder} will be used to support custom locating annotations.
 * It is not used for basic locating annotation of selenium.
 * </p>
 * 
 * @author Thomas
 *
 * @see Annotations
 */
public interface LocatorBuilderFactory {

	/**
	 * This method gets an instance for provided {@code builderClass}.
	 * 
	 * @param builderClass
	 * @return returns an existing instance for given {@code builderClass} or
	 *         creates new one.
	 */
	@SuppressWarnings("rawtypes")
	public LocatorBuilder get(Class<? extends LocatorBuilder> builderClass);
}
