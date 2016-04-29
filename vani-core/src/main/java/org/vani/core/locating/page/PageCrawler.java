package org.vani.core.locating.page;

import org.openqa.selenium.WebDriver;
import org.vani.core.annotation.PageHandler;

/**
 * A page crawler will collect all applicable urls and opens it. After that it
 * will collect all applicable urls again. So you can implement a simple
 * crawler.
 * <h3>Using</h3>
 * <p>
 * To use, you only have to inject {@link PageCrawler} and call
 * {@link PageCrawler#start()}:
 * </p>
 * 
 * <pre>
 * <code>
 * &#64;Autowired
 * private PageCrawler pageCrawler;
 * &#64;Test
 * public void browse() {
 * 	//webDriver will be autowired by spring
 * 	pageCrawler.start();
 * 
 * 	//or specify webDriver
 * 	pageCrawler.start(webDriver);
 * }
 * </code>
 * </pre>
 * <p>
 * Additionally, you must declare a page handler with url mappings. For that see
 * {@link PageHandler}.
 * </p>
 * 
 * @author Thomas
 *
 */
public interface PageCrawler {

	/**
	 * This method will crawl all applicable urls with specified
	 * {@link WebDriver}.
	 * 
	 * @param webDriver
	 */
	public void start(WebDriver webDriver);

	/**
	 * This method will crawl all applicable urls with {@link WebDriver}
	 * resolved by spring context.
	 */
	public void start();
}
