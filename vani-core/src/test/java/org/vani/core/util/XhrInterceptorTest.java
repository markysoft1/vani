package org.vani.core.util;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;
import org.vani.core.VaniContext;
import org.vani.core.annotation.Xhr;
import org.vani.core.wait.WaitUtil;

@RunWith(MockitoJUnitRunner.class)
public class XhrInterceptorTest {
	private XhrInterceptor bean;

	@Mock
	protected VaniContext vaniContext;
	@Mock
	protected WebDriver webDriver;
	@Mock
	protected WaitUtil waitUtil;

	@Mock
	protected Callable<Object> zuper;
	@Mock
	protected ApplicationContext appContext;

	@Before
	public void setUp() {
		when(vaniContext.getAppContext()).thenReturn(appContext);
		when(appContext.getBean(WaitUtil.class)).thenReturn(waitUtil);

		bean = new XhrInterceptor(vaniContext, webDriver);
	}

	/**
	 * tests {@link XhrInterceptor#intercept(Method, Callable)} when call
	 * returns false and {@link Xhr#disabledByReturn()} is true.
	 * <p>
	 * As result, false must be returned and waiting must be skipped.
	 * </p>
	 */
	@Test
	public void testInterceptSkippingWithBoolean() throws Throwable {
		System.out.println("testInterceptSkippingWithBoolean");

		when(zuper.call()).thenReturn(false);
		Method method = TestingMethods.class.getDeclaredMethod("method");

		Object result = bean.intercept(method, zuper);

		verify(vaniContext, times(0)).resolveExpression(anyString());
		Assert.assertEquals("wrong result: ", Boolean.FALSE, result);
	}

	/**
	 * tests {@link XhrInterceptor#intercept(Method, Callable)} when call
	 * returns empty literal and {@link Xhr#disabledByReturn()} is true.
	 * <p>
	 * As result, empty literal must be returned and waiting must be skipped.
	 * </p>
	 */
	@Test
	public void testInterceptSkippingWithEmpty() throws Throwable {
		System.out.println("testInterceptSkippingWithEmpty");

		when(zuper.call()).thenReturn("");
		Method method = TestingMethods.class.getDeclaredMethod("method");

		Object result = bean.intercept(method, zuper);

		verify(vaniContext, times(0)).resolveExpression(anyString());
		Assert.assertEquals("wrong result: ", "", result);
	}

	/**
	 * tests {@link XhrInterceptor#intercept(Method, Callable)} when call
	 * returns {@code NULL} and {@link Xhr#disabledByReturn()} is true.
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

		verify(vaniContext, times(0)).resolveExpression(anyString());
		Assert.assertEquals("wrong result: ", null, result);
	}

	/**
	 * tests {@link XhrInterceptor#intercept(Method, Callable)} when call
	 * returns {@code 0} and {@link Xhr#disabledByReturn()} is true.
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

		verify(vaniContext, times(0)).resolveExpression(anyString());
		Assert.assertEquals("wrong result: ", 0, result);
	}

	/**
	 * tests {@link XhrInterceptor#intercept(Method, Callable)} when call
	 * returns {@code 0} and {@link Xhr#disabledByReturn()} is false.
	 * <p>
	 * As result, {@code 0} must be returned and waiting should be executed,
	 * because {@link Xhr#disabledByReturn()} is false.
	 * </p>
	 */
	@Test
	public void testInterceptWithDisabledSkippingWithInt() throws Throwable {
		System.out.println("testInterceptWithDisabledSkippingWithInt");

		when(zuper.call()).thenReturn(0);
		Method method = TestingMethods.class.getDeclaredMethod("method2");
		String expectedUrl = "http://www.master-blog.com";
		when(vaniContext.resolveExpression(expectedUrl)).thenReturn(expectedUrl);

		Object result = bean.intercept(method, zuper);

		verify(vaniContext, times(1)).resolveExpression(expectedUrl);
		verify(waitUtil, times(1)).ajaxJQuery(eq(expectedUrl), anyLong(), eq((long) 30 * 1000), eq(webDriver));
		Assert.assertEquals("wrong result: ", 0, result);
	}

	/**
	 * tests {@link XhrInterceptor#intercept(Method, Callable)} when call
	 * returns number > 0 and {@link Xhr#disabledByReturn()} is true.
	 * <p>
	 * As result, correct value must be returned and waiting should be executed.
	 * </p>
	 */
	@Test
	public void testInterceptWithInt() throws Throwable {
		System.out.println("testInterceptWithInt");

		when(zuper.call()).thenReturn(1250);
		Method method = TestingMethods.class.getDeclaredMethod("method");
		String expectedUrl = "http://www.master-blog.com";
		when(vaniContext.resolveExpression(expectedUrl)).thenReturn(expectedUrl);

		Object result = bean.intercept(method, zuper);

		verify(vaniContext, times(1)).resolveExpression(expectedUrl);
		verify(waitUtil, times(1)).ajaxJQuery(eq(expectedUrl), anyLong(), eq((long) 30 * 1000), eq(webDriver));
		Assert.assertEquals("wrong result: ", 1250, result);
	}

	class TestingMethods {

		@Xhr(value = "http://www.master-blog.com", disabledByReturn = true)
		String method() {
			return "";
		}

		@Xhr(value = "http://www.master-blog.com")
		String method2() {
			return "";
		}
	}

}
