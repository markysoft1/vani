package org.markysoft.vani.core.locating.factory;

import org.markysoft.vani.core.annotation.Page;
import org.markysoft.vani.core.locating.FragmentObject;
import org.markysoft.vani.core.locating.PageObject;
import org.markysoft.vani.core.locating.RegionObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * This interface is responsible for creating instance of {@link RegionObject},
 * {@link PageObject} or {@link FragmentObject}.
 * 
 * @author Thomas
 *
 */
public interface RegionFactory {

	/**
	 * creates new region object of given class and use given {@code webElement}
	 * as rootElement.
	 * 
	 * @param regionClass
	 *            class which should be created
	 * @param rootElement
	 *            root element for desired {@link RegionObject}
	 * @return returns object of given region class and root element with
	 *         injected dependencies
	 */
	public <T> T create(Class<T> regionClass, WebElement rootElement);

	/**
	 * creates new region object of given class and use given {@code webDriver}
	 * 
	 * @param regionClass
	 *            class which should be created
	 * @param webDriver
	 *            webDriver, which should be used by {@link RegionObject}
	 * @return returns object of given region class and use specified
	 *         {@code webDriver} with injected dependencies
	 */
	public <T> T create(Class<T> regionClass, WebDriver webDriver);

	public <T> T create(Class<T> regionClass, WebDriver webDriver, WebElement rootElement);

	public <T> T create(Class<T> regionClass, WebDriver webDriver, Page pageAnnotation);

	public <T> T createPage(Class<T> pageClass, WebDriver webDriver, String pageUrl);

	public <T> T create(Class<T> regionClass, Page pageAnnotation);
}
