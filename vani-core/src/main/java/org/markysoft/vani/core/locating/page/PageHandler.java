package org.markysoft.vani.core.locating.page;

import java.util.Set;

import org.markysoft.vani.core.annotation.UrlMapping;
import org.openqa.selenium.WebDriver;

/**
 * The {@link PageHandler} is responsible for calling correct handler method for
 * specified url.
 * <p>
 * For information about declaring {@link PageHandler} and its handler methods
 * see {@link org.markysoft.vani.core.annotation.PageHandler} and {@link UrlMapping}.
 * 
 * @author Thomas
 *
 * @param <T>
 *            underlying handler
 */
public interface PageHandler<T> {

	/**
	 * Method checks whether underlying handler has a handler method for
	 * provided url.
	 * 
	 * @param url
	 * @return returns true if underlying handler has appropriate url mapping,
	 *         else false
	 */
	public boolean isApplicable(String url);

	/**
	 * This method will look for the best matching handler and invoke it.
	 * 
	 * @param url
	 * @param webDriver
	 */
	public void handle(String url, WebDriver webDriver);

	/**
	 * 
	 * @return returns all url patterns declared by underlying handler
	 */
	public Set<String> getUrlPatterns();
}
