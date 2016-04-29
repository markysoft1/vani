package org.vani.core.condition;

import java.util.function.Predicate;

import org.openqa.selenium.WebElement;

/**
 * This class provides static utility methods, which can used as
 * {@link Predicate}
 */
public class Is {

	/**
	 * This method will check whether provided {@link WebElement} is displayed.
	 * 
	 * @param element
	 * @return returns true if provided element is displayed, else false (also
	 *         when provided parameter is no {@link WebElement})
	 * @see WebElement#isDisplayed()
	 */
	public static boolean displayed(Object element) {
		boolean result = element instanceof WebElement ? ((WebElement) element).isDisplayed() : false;
		return result;
	}

	/**
	 * This method will check whether provided {@link WebElement} is selected.
	 * 
	 * @param element
	 * @return returns true if provided element is selected, else false (also
	 *         when provided parameter is no {@link WebElement})
	 * @see WebElement#isSelected()
	 */
	public static boolean selected(Object element) {
		boolean result = element instanceof WebElement ? ((WebElement) element).isSelected() : false;
		return result;
	}

	/**
	 * This method will check whether provided {@link WebElement} is enabled.
	 * 
	 * @param element
	 * @return returns true if provided element is enabled, else false (also
	 *         when provided parameter is no {@link WebElement})
	 * @see WebElement#isEnabled()
	 */
	public static boolean enabled(Object element) {
		boolean result = element instanceof WebElement ? ((WebElement) element).isEnabled() : false;
		return result;
	}

	/**
	 * This method will check whether provided {@link WebElement} is disabled.
	 * 
	 * @param element
	 * @return returns true if provided element is disabled, else false (also
	 *         when provided parameter is no {@link WebElement})
	 * @see WebElement#isEnabled()
	 */
	public static boolean disabled(Object element) {
		boolean result = element instanceof WebElement && !enabled(element);
		return result;
	}

}
