package org.markysoft.vani.core.condition;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebElement;

@RunWith(MockitoJUnitRunner.class)
public class IsTest {

	@Mock
	private WebElement webElement;
	@Mock
	private Object object;

	/**
	 * tests {@link Is#displayed(Object)} with non {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code false} should be returned, because non webElement is
	 * provided
	 * </p>
	 */
	@Test
	public void testDisplayedWithNonWebElement() {
		System.out.println("testDisplayedWithNonWebElement");

		boolean result = Is.displayed(object);

		Assert.assertFalse("false must be returned because of parameter is no webElement!", result);
	}

	/**
	 * tests {@link Is#displayed(Object)} with invisible {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code false} should be returned
	 * </p>
	 */
	@Test
	public void testDisplayedWithInvisibleWebElement() {
		System.out.println("testDisplayedWithInvisibleWebElement");

		boolean result = Is.displayed(webElement);

		Assert.assertFalse("false must be returned because of webElement is not visible!", result);
	}

	/**
	 * tests {@link Is#displayed(Object)} with visible {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code true} should be returned
	 * </p>
	 */
	@Test
	public void testDisplayedWithVisibleWebElement() {
		System.out.println("testDisplayedWithVisibleWebElement");

		when(webElement.isDisplayed()).thenReturn(true);
		boolean result = Is.displayed(webElement);

		Assert.assertTrue("true must be returned because of webElement is visible!", result);
	}

	/**
	 * tests {@link Is#selected(Object)} with non {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code false} should be returned, because non webElement is
	 * provided
	 * </p>
	 */
	@Test
	public void testSelectedWithNonWebElement() {
		System.out.println("testSelectedWithNonWebElement");

		boolean result = Is.selected(object);

		Assert.assertFalse("false must be returned because of parameter is no webElement!", result);
	}

	/**
	 * tests {@link Is#selected(Object)} with unselected {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code false} should be returned
	 * </p>
	 */
	@Test
	public void testSelectedWithUnselectedWebElement() {
		System.out.println("testSelectedWithUnselectedWebElement");

		boolean result = Is.selected(webElement);

		Assert.assertFalse("false must be returned because of webElement is not selected!", result);
	}

	/**
	 * tests {@link Is#selected(Object)} with selected {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code true} should be returned
	 * </p>
	 */
	@Test
	public void testSelectedWithSelectedWebElement() {
		System.out.println("testSelectedWithSelectedWebElement");

		when(webElement.isSelected()).thenReturn(true);
		boolean result = Is.selected(webElement);

		Assert.assertTrue("true must be returned because of webElement is selected!", result);
	}

	/**
	 * tests {@link Is#enabled(Object)} with non {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code false} should be returned, because non webElement is
	 * provided
	 * </p>
	 */
	@Test
	public void testEnabledWithNonWebElement() {
		System.out.println("testEnabledWithNonWebElement");

		boolean result = Is.enabled(object);

		Assert.assertFalse("false must be returned because of parameter is no webElement!", result);
	}

	/**
	 * tests {@link Is#enabled(Object)} with disabled {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code false} should be returned
	 * </p>
	 */
	@Test
	public void testEnabledWithDisabledWebElement() {
		System.out.println("testEnabledWithDisabledWebElement");

		boolean result = Is.enabled(webElement);

		Assert.assertFalse("false must be returned because of webElement is not enabled!", result);
	}

	/**
	 * tests {@link Is#enabled(Object)} with enabled {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code true} should be returned
	 * </p>
	 */
	@Test
	public void testEnabledWithEnabledWebElement() {
		System.out.println("testEnabledWithEnabledWebElement");

		when(webElement.isEnabled()).thenReturn(true);
		boolean result = Is.enabled(webElement);

		Assert.assertTrue("true must be returned because of webElement is enabled!", result);
	}

	/**
	 * tests {@link Is#disabled(Object)} with non {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code false} should be returned, because non webElement is
	 * provided
	 * </p>
	 */
	@Test
	public void testDisabledWithNonWebElement() {
		System.out.println("testDisabledWithNonWebElement");

		boolean result = Is.disabled(object);

		Assert.assertFalse("false must be returned because of parameter is no webElement!", result);
	}

	/**
	 * tests {@link Is#disabled(Object)} with enabled {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code false} should be returned
	 * </p>
	 */
	@Test
	public void testDisabledWithEnabledWebElement() {
		System.out.println("testDisabledWithEnabledWebElement");

		when(webElement.isEnabled()).thenReturn(true);
		boolean result = Is.disabled(webElement);

		Assert.assertFalse("false must be returned because of webElement is not disabled!", result);
	}

	/**
	 * tests {@link Is#disabled(Object)} with disabled {@link WebElement} as
	 * parameter.
	 * <p>
	 * As result, {@code true} should be returned
	 * </p>
	 */
	@Test
	public void testDisabledWithDisabledWebElement() {
		System.out.println("testDisabledWithDisabledWebElement");

		boolean result = Is.disabled(webElement);

		Assert.assertTrue("true must be returned because of webElement is disabled!", result);
	}

	/**
	 * tests {@link Is#present(Object)} with {@code NULL} as parameter.
	 * <p>
	 * As result, {@code false} should be returned.
	 * </p>
	 */
	@Test
	public void testPresentWithNull() {
		System.out.println("testPresentWithNull");

		boolean result = Is.present(null);

		Assert.assertFalse("false must be returned because of given value is NULL!", result);
	}

	/**
	 * tests {@link Is#present(Object)} with empty literal as parameter.
	 * <p>
	 * As result, {@code false} should be returned.
	 * </p>
	 */
	@Test
	public void testPresentWithEmptyString() {
		System.out.println("testPresentWithEmptyString");

		boolean result = Is.present("");

		Assert.assertFalse("false must be returned because of given string is empty!", result);
	}

	/**
	 * tests {@link Is#present(Object)} with not {@code NULL} value as
	 * parameter.
	 * <p>
	 * As result, {@code true} should be returned.
	 * </p>
	 */
	@Test
	public void testPresentReturningTrue() {
		System.out.println("testPresentReturningTrue");

		boolean result = Is.present(5);

		Assert.assertTrue("true must be returned because of given value is not NULL!", result);
	}

	/**
	 * tests {@link Is#present(Object)} with false as parameter.
	 * <p>
	 * As result, {@code false} should be returned.
	 * </p>
	 */
	@Test
	public void testPresentWithBooleanFalse() {
		System.out.println("testPresentWithBooleanFalse");

		boolean result = Is.present(false);

		Assert.assertFalse("false must be returned because of given value is false!", result);
	}

	/**
	 * tests {@link Is#present(Object)} with true as parameter.
	 * <p>
	 * As result, {@code false} should be returned.
	 * </p>
	 */
	@Test
	public void testPresentWithBooleanTrue() {
		System.out.println("testPresentWithBooleanTrue");

		boolean result = Is.present(true);

		Assert.assertTrue("true must be returned because of given value is true!", result);
	}
}
