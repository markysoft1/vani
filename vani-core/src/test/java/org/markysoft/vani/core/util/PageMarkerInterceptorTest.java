package org.markysoft.vani.core.util;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.PageMarker;
import org.markysoft.vani.core.locating.PageMarkerHandler;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class PageMarkerInterceptorTest {
	private PageMarkerInterceptor bean;

	@Mock
	protected VaniContext vaniContext;
	@Mock
	protected WebDriver webDriver;
	@Mock
	protected PageMarkerHandler pageMarkerHandler;

	@Mock
	protected Callable<Object> zuper;
	@Mock
	protected ApplicationContext appContext;

	@Before
	public void setUp() {
		when(vaniContext.getAppContext()).thenReturn(appContext);
		when(appContext.getBean(PageMarkerHandler.class)).thenReturn(pageMarkerHandler);

		bean = new PageMarkerInterceptor(vaniContext, webDriver);
	}

	/**
	 * tests {@link PageMarkerInterceptor#intercept(Method, Callable)} when call
	 * returns false and {@link PageMarker#disabledByReturn()} is true.
	 * <p>
	 * As result, false must be returned and waiting must be skipped.
	 * </p>
	 */
	@Test
	public void testInterceptSkippingWithBoolean() throws Throwable {
		System.out.println("testInterceptSkippingWithBoolean");

		when(zuper.call()).thenReturn(false);
		when(vaniContext.resolveExpression("pageMarker")).thenReturn("pageMarkerResolved");
		Method method = TestingMethods.class.getDeclaredMethod("method");

		Object result = bean.intercept(method, zuper);

		verify(vaniContext, times(1)).resolveExpression(anyString());
		Assert.assertEquals("wrong result: ", Boolean.FALSE, result);
	}

	/**
	 * tests {@link PageMarkerInterceptor#intercept(Method, Callable)} when call
	 * returns empty literal and {@link PageMarker#disabledByReturn()} is true.
	 * <p>
	 * As result, empty literal must be returned and waiting must be skipped.
	 * </p>
	 */
	@Test
	public void testInterceptSkippingWithEmpty() throws Throwable {
		System.out.println("testInterceptSkippingWithEmpty");

		when(zuper.call()).thenReturn("");
		when(vaniContext.resolveExpression("pageMarker")).thenReturn("pageMarkerResolved");
		Method method = TestingMethods.class.getDeclaredMethod("method");

		Object result = bean.intercept(method, zuper);

		verify(vaniContext, times(1)).resolveExpression(anyString());
		Assert.assertEquals("wrong result: ", "", result);
	}

	/**
	 * tests {@link PageMarkerInterceptor#intercept(Method, Callable)} when call
	 * returns {@code NULL} and {@link PageMarker#disabledByReturn()} is true.
	 * <p>
	 * As result, {@code NULL} must be returned and waiting must be skipped.
	 * </p>
	 */
	@Test
	public void testInterceptSkippingWithNull() throws Throwable {
		System.out.println("testInterceptSkippingWithNull");

		when(zuper.call()).thenReturn(null);
		Method method = TestingMethods.class.getDeclaredMethod("method");

		Object result = bean.intercept(method, zuper);

		verify(vaniContext, times(1)).resolveExpression(anyString());
		Assert.assertEquals("wrong result: ", null, result);
	}

	/**
	 * tests {@link PageMarkerInterceptor#intercept(Method, Callable)} when call
	 * returns {@code 0} and {@link PageMarker#disabledByReturn()} is true.
	 * <p>
	 * As result, {@code 0} must be returned and waiting must be skipped.
	 * </p>
	 */
	@Test
	public void testInterceptSkippingWithInt() throws Throwable {
		System.out.println("testInterceptSkippingWithInt");

		when(zuper.call()).thenReturn(0);
		Method method = TestingMethods.class.getDeclaredMethod("method");

		Object result = bean.intercept(method, zuper);

		verify(vaniContext, times(1)).resolveExpression(anyString());
		Assert.assertEquals("wrong result: ", 0, result);
	}

	/**
	 * tests {@link PageMarkerInterceptor#intercept(Method, Callable)} when call
	 * returns {@code 0} and {@link PageMarker#disabledByReturn()} is false.
	 * <p>
	 * As result, {@code 0} must be returned and waiting should be executed,
	 * because {@link PageMarker#disabledByReturn()} is false.
	 * </p>
	 */
	@Test
	public void testInterceptWithDisabledSkippingWithInt() throws Throwable {
		System.out.println("testInterceptWithDisabledSkippingWithInt");

		when(zuper.call()).thenReturn(0);
		Method method = TestingMethods.class.getDeclaredMethod("method2");
		String markerName = "pageMarker";
		String expectedMarkerName = "pageMarkerResolved";
		when(vaniContext.resolveExpression(markerName)).thenReturn(expectedMarkerName);

		Object result = bean.intercept(method, zuper);

		verify(vaniContext, times(1)).resolveExpression(markerName);
		verify(pageMarkerHandler, times(1)).waitUntilMarkerIsPresent(expectedMarkerName, 30 * 1000, webDriver);
		Assert.assertEquals("wrong result: ", 0, result);
	}

	/**
	 * tests {@link PageMarkerInterceptor#intercept(Method, Callable)} when call
	 * returns number > 0 and {@link PageMarker#disabledByReturn()} is true.
	 * <p>
	 * As result, correct value must be returned and waiting should be executed.
	 * </p>
	 */
	@Test
	public void testInterceptWithInt() throws Throwable {
		System.out.println("testInterceptWithInt");

		when(zuper.call()).thenReturn(1250);
		Method method = TestingMethods.class.getDeclaredMethod("method");
		String markerName = "pageMarker";
		String expectedMarkerName = "pageMarkerResolved";
		when(vaniContext.resolveExpression(markerName)).thenReturn(expectedMarkerName);

		Object result = bean.intercept(method, zuper);

		verify(vaniContext, times(1)).resolveExpression(markerName);
		verify(pageMarkerHandler, times(1)).waitUntilMarkerIsPresent(expectedMarkerName, 30 * 1000, webDriver);
		Assert.assertEquals("wrong result: ", 1250, result);
	}

	class TestingMethods {

		@PageMarker(value = "pageMarker", disabledByReturn = true)
		String method() {
			return "";
		}

		@PageMarker(value = "pageMarker")
		String method2() {
			return "";
		}
	}

}
