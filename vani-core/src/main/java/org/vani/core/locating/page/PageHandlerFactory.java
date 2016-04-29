package org.vani.core.locating.page;

import org.vani.core.annotation.UrlMapping;

/**
 * This class is responsible for generating the wrapper for annotated page
 * handlers of vani's crawling mechanism.
 * 
 * <p>
 * Your page handler will be wrapped by an implementation of {@link PageHandler}
 * . For that, the mapping between url pattern and corresponding methods must be
 * set.
 * </p>
 * 
 * @author Thomas
 * @see PageHandler
 * @see org.vani.core.annotation.PageHandler
 * @see UrlMapping
 */
public interface PageHandlerFactory {
	/**
	 * Method for creating wrapper for provided handler class.
	 * 
	 * @param handlerClass
	 * @return returns full initialised wrapper with instance of desired handler
	 *         class.
	 */
	public <T> PageHandler<T> create(Class<T> handlerClass);

}
