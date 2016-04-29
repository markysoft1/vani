package org.vani.core.locating;

import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Base class for all ElementLocator implementations. It replace the selenium's
 * structure to support additionally types and not only {@link WebElement}.
 * 
 * @author Thomas
 *
 * @param <T>
 *            target type (eg.: WebElement)
 */
public interface VaniElementLocator<T> {
	/**
	 * This method will look only for one match and return it.
	 * 
	 * @return returns found match or throws an exception when no matching is
	 *         available
	 * @throws NoSuchElementException
	 *             will be thrown if no matching can be found
	 */
	T findElement() throws NoSuchElementException;

	/**
	 * This method will look for multiple matches and returns it as a list.
	 * 
	 * @return returns all matches as list or empty list if no matches are
	 *         available.
	 */
	List<T> findElements();

}
