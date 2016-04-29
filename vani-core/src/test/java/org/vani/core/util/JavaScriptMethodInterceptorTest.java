package org.vani.core.util;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;
import org.vani.core.VaniContext;
import org.vani.core.annotation.GlobalReference;
import org.vani.core.annotation.JavaScriptFunction;
import org.vani.core.annotation.JsFunctionArguments;
import org.vani.core.annotation.JsFunctionName;
import org.vani.core.javascript.GlobalReferenceHolder;
import org.vani.core.javascript.JQueryTypeHandler;
import org.vani.core.javascript.JavaScriptCallFunction;
import org.vani.core.javascript.JavaScriptSource;
import org.vani.core.javascript.TypeHandler;
import org.vani.core.javascript.VaniJavaScriptExecutor;
import org.vani.core.locating.JQueryElement;

@RunWith(MockitoJUnitRunner.class)
public class JavaScriptMethodInterceptorTest {
	private JavaScriptMethodInterceptor bean;

	@Mock
	private JavaScriptSource<?> jsSource;
	@Mock
	private VaniContext vaniContext;
	private List<Object> manualJSInterfaceImplemenations;

	@Mock
	protected JQueryElement $element;
	@Mock
	protected JQueryTypeHandler $typeHandler;
	@Mock
	private WebDriver webDriver;
	@Mock
	private JavascriptExecutor jsExecutor;
	@Mock
	private ApplicationContext appContext;
	@Mock
	private JavaScriptFunction jsFunctionAnnotation;
	@Mock
	private JavaScriptCallFunction jsCallFunction;
	@Mock
	private TestWebDriver testWebDriver;

	@Before
	public void setUp() {
		manualJSInterfaceImplemenations = new ArrayList<>();
		bean = new JavaScriptMethodInterceptor(jsSource, vaniContext, manualJSInterfaceImplemenations);
		when(vaniContext.getAppContext()).thenReturn(appContext);
		when(appContext.getBean(WebDriver.class)).thenReturn(testWebDriver);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#handleResult(Object, Class, WebDriver)}
	 * when no {@link TypeHandler} is available for provided return-type.
	 * <p>
	 * As result, provided value must be returned, because no
	 * {@link TypeHandler} is available.
	 * </p>
	 */
	@Test
	public void testHandleResultWithoutTypeHandler() {
		System.out.println("testHandleResultWithoutTypeHandler");

		String resultParam = "fire";

		String result = bean.handleResult(resultParam, String.class, webDriver);

		verify(vaniContext, times(1)).getTypeHandlerFor(String.class);
		Assert.assertEquals("wrong result: ", resultParam, result);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#handleResult(Object, Class, WebDriver)}
	 * when {@link TypeHandler} is available for provided return-type.
	 * <p>
	 * As result, converted value must be returned, because a
	 * {@link TypeHandler} is available.
	 * </p>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testHandleResultWithTypeHandler() {
		System.out.println("testHandleResultWithTypeHandler");

		TypeHandler typeHandler = $typeHandler;
		String resultParam = "fire";
		when(vaniContext.getTypeHandlerFor(anyObject())).thenReturn(typeHandler);
		when($typeHandler.get(resultParam, webDriver)).thenReturn($element);

		JQueryElement result = bean.handleResult(resultParam, JQueryElement.class, webDriver);

		verify(vaniContext, times(1)).getTypeHandlerFor(JQueryElement.class);
		Assert.assertEquals("wrong result: ", $element, result);
	}

	/**
	 * tests {@link JavaScriptMethodInterceptor#getExecutor(JavascriptExecutor)}
	 * when a {@link JavascriptExecutor} is provided as parameter.
	 * <p>
	 * As result, provided parameter wrapped by {@link VaniJavaScriptExecutor}
	 * must be returned.
	 * </p>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetExecutor() {
		System.out.println("testGetExecutor");

		VaniJavaScriptExecutor result = bean.getExecutor(jsExecutor);

		verify(vaniContext, times(0)).getAppContext();
		Assert.assertEquals("wrong result: ", jsExecutor, result.getWrappedExecutor());
	}

	/**
	 * tests {@link JavaScriptMethodInterceptor#getExecutor(JavascriptExecutor)}
	 * when {@code NULL} is provided as parameter.
	 * <p>
	 * As result, {@link WebDriver} instance from application context must be
	 * returned.
	 * </p>
	 */
	@Test
	public void testGetExecutorWithNull() {
		System.out.println("testGetExecutorWithNull");

		when(appContext.getBean(WebDriver.class)).thenReturn(testWebDriver);

		VaniJavaScriptExecutor result = bean.getExecutor(null);

		verify(vaniContext, times(1)).getAppContext();
		Assert.assertEquals("wrong result: ", testWebDriver, result.getWrappedExecutor());
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#filterArguments(Method, Object[], java.util.Map, Class...)}
	 * when {@code NULL} is used as parameter.
	 * <p>
	 * As result, {@code NULL} must be returned, because no arguments were
	 * specified
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFilterArgumentsWithoutArguments() throws Exception {
		System.out.println("testFilterArgumentsWithoutArguments");

		Method jsMethod = getClass().getDeclaredMethod("javaScriptMethod");

		Object result = bean.filterArguments(jsMethod, null, new HashMap<>(), WebDriver.class);

		Assert.assertEquals("wrong result", null, result);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#filterArguments(Method, Object[], java.util.Map, Class...)}
	 * when arguments are specified but no filters are available.
	 * <p>
	 * As result, provided arguments must be returned, because no filters were
	 * specified
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFilterArgumentsWithoutFilters() throws Exception {
		System.out.println("testFilterArgumentsWithoutFilters");

		Method jsMethod = getClass().getDeclaredMethod("javaScriptMethod", String.class, WebDriver.class,
				Integer.class);
		Object[] args = new Object[] { "fire", webDriver, 5248 };

		Object[] result = bean.filterArguments(jsMethod, args, new HashMap<>());

		Assert.assertArrayEquals("wrong result: ", args, result);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#filterArguments(Method, Object[], java.util.Map, Class...)}
	 * when arguments and filters are available.
	 * <p>
	 * As result,result must only contains not filter applicable entries and the
	 * provided map should contains the filtered instances.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFilterArguments() throws Exception {
		System.out.println("testFilterArguments");

		Method jsMethod = getClass().getDeclaredMethod("javaScriptMethod", String.class, WebDriver.class, Integer.class,
				JQueryElement.class);
		Object[] args = new Object[] { "fire", webDriver, 55, $element };
		Map<Class<?>, Object> filteredEntries = new HashMap<>();
		Object[] result = bean.filterArguments(jsMethod, args, filteredEntries, WebDriver.class,
				GlobalReferenceHolder.class);

		Assert.assertArrayEquals("wrong result", new Object[] { "fire", 55 }, result);

		Assert.assertEquals("wrong filtered webDriver: ", webDriver, filteredEntries.get(WebDriver.class));
		Assert.assertEquals("wrong filtered globalReference: ", $element,
				filteredEntries.get(GlobalReferenceHolder.class));
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#prepareArgumentsForCustomCallFunc(Object[], Map, String, JavaScriptFunction)}
	 * when current jsSource has no call function.
	 * <p>
	 * As result,provided arguments must be returned, because there is no call
	 * function available.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPrepareArgumentsForCustomCallFuncWithoutCall() throws Exception {
		System.out.println("testPrepareArgumentsForCustomCallFuncWithoutCall");

		Object[] arguments = new Object[] { "something", "fire" };
		Map<Class<?>, Object> filteredArguments = new HashMap<>();
		String jsMethodName = "";

		Object[] result = bean.prepareArgumentsForCustomCallFunc(arguments, filteredArguments, jsMethodName,
				jsFunctionAnnotation);

		Assert.assertArrayEquals("wrong result", arguments, result);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#prepareArgumentsForCustomCallFunc(Object[], Map, String, JavaScriptFunction)}
	 * when call function has no arguments.
	 * <p>
	 * As result,empty array must be returned.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPrepareArgumentsForCustomCallFuncWithoutArgs() throws Exception {
		System.out.println("testPrepareArgumentsForCustomCallFuncWithoutArgs");

		Object[] arguments = new Object[] { "something", "fire" };
		Map<Class<?>, Object> filteredArguments = new HashMap<>();
		String jsMethodName = "javaScriptMethod";
		Method jsMethod = getClass().getDeclaredMethod("javaScriptMethod");
		when(jsSource.getJsCallFunction()).thenReturn(jsCallFunction);
		when(jsCallFunction.getCallMethod()).thenReturn(jsMethod);

		Object[] result = bean.prepareArgumentsForCustomCallFunc(arguments, filteredArguments, jsMethodName,
				jsFunctionAnnotation);

		Assert.assertArrayEquals("wrong result", new Object[] {}, result);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#prepareArgumentsForCustomCallFunc(Object[], Map, String, JavaScriptFunction)}
	 * when call function has arguments.
	 * <p>
	 * As result,array must contains global reference string, calling js
	 * function and provided arguments array.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPrepareArgumentsForCustomCallFuncWithArgs() throws Exception {
		System.out.println("testPrepareArgumentsForCustomCallFuncWithArgs");

		Object[] arguments = new Object[] { "something", 55 };
		Map<Class<?>, Object> filteredArguments = new HashMap<>();
		filteredArguments.put(GlobalReferenceHolder.class, $element);
		String jsFunctionName = "fire";
		when(jsFunctionAnnotation.name()).thenReturn(jsFunctionName);
		String referenceValue = "645-4546548-645465";
		when($element.getReference()).thenReturn(referenceValue);
		String jsMethodName = "callFunction";
		Method jsMethod = getClass().getDeclaredMethod("callFunction", String.class, String.class, Object[].class);
		when(jsSource.getJsCallFunction()).thenReturn(jsCallFunction);
		when(jsCallFunction.getCallMethod()).thenReturn(jsMethod);

		Object[] result = bean.prepareArgumentsForCustomCallFunc(arguments, filteredArguments, jsMethodName,
				jsFunctionAnnotation);

		Assert.assertArrayEquals("wrong result", new Object[] { referenceValue, jsFunctionName, arguments }, result);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#prepareArgumentsForCustomCallFunc(Object[], Map, String, JavaScriptFunction)}
	 * when call function has no annotated parameters.
	 * <p>
	 * As result,array must be empty, because vani is not able to resolve the
	 * required parameters without annotation.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPrepareArgumentsForCustomCallFuncWithoutAnnotatedArgs() throws Exception {
		System.out.println("testPrepareArgumentsForCustomCallFuncWithoutAnnotatedArgs");

		Object[] arguments = new Object[] { "something", 55 };
		Map<Class<?>, Object> filteredArguments = new HashMap<>();
		filteredArguments.put(GlobalReferenceHolder.class, $element);
		String jsFunctionName = "fire";
		when(jsFunctionAnnotation.name()).thenReturn(jsFunctionName);
		String referenceValue = "645-4546548-645465";
		when($element.getReference()).thenReturn(referenceValue);
		String jsMethodName = "callFunction";
		Method jsMethod = getClass().getDeclaredMethod("callFunctionWithoutAnnotatedParams", String.class, String.class,
				Integer.class);
		when(jsSource.getJsCallFunction()).thenReturn(jsCallFunction);
		when(jsCallFunction.getCallMethod()).thenReturn(jsMethod);

		Object[] result = bean.prepareArgumentsForCustomCallFunc(arguments, filteredArguments, jsMethodName,
				jsFunctionAnnotation);

		Assert.assertArrayEquals("wrong result", new Object[] {}, result);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#prepareArgumentsForCustomCallFunc(Object[], Map, String, JavaScriptFunction)}
	 * when call function has annotated parameters, but filtered
	 * {@link GlobalReferenceHolder} is {@code NULL} and
	 * {@link JavaScriptFunction} declares no name.
	 * <p>
	 * As result, array must contains {@code NULL} as global reference string,
	 * js method name and provided arguments array.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPrepareArgumentsForCustomCallFuncWithoutFunctionNameValueAndReference() throws Exception {
		System.out.println("testPrepareArgumentsForCustomCallFuncWithoutFunctionNameValueAndReference");

		Object[] arguments = new Object[] { "something", 55 };
		Map<Class<?>, Object> filteredArguments = new HashMap<>();
		filteredArguments.put(GlobalReferenceHolder.class, null);
		String jsMethodName = "fire";
		Method jsMethod = getClass().getDeclaredMethod("callFunction", String.class, String.class, Object[].class);
		when(jsSource.getJsCallFunction()).thenReturn(jsCallFunction);
		when(jsCallFunction.getCallMethod()).thenReturn(jsMethod);

		Object[] result = bean.prepareArgumentsForCustomCallFunc(arguments, filteredArguments, jsMethodName,
				jsFunctionAnnotation);

		Assert.assertArrayEquals("wrong result", new Object[] { null, jsMethodName, arguments }, result);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#prepareArgumentsForCustomCallFunc(Object[], Map, String, JavaScriptFunction)}
	 * when call function has annotated parameters, but filtered
	 * {@link GlobalReferenceHolder} is {@code NULL} and
	 * {@link JavaScriptFunction} declares value instead of name.
	 * <p>
	 * As result, array must contains {@code NULL} as global reference string
	 * and as js method name and provided arguments array.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPrepareArgumentsForCustomCallFuncWithFunctionSouce() throws Exception {
		System.out.println("testPrepareArgumentsForCustomCallFuncWithFunctionSouce");

		Object[] arguments = new Object[] { "something", 55 };
		Map<Class<?>, Object> filteredArguments = new HashMap<>();
		filteredArguments.put(GlobalReferenceHolder.class, null);
		String jsMethodName = "fire";
		Method jsMethod = getClass().getDeclaredMethod("callFunction", String.class, String.class, Object[].class);
		when(jsSource.getJsCallFunction()).thenReturn(jsCallFunction);
		when(jsCallFunction.getCallMethod()).thenReturn(jsMethod);
		when(jsFunctionAnnotation.value()).thenReturn("alert('haha :P');");

		Object[] result = bean.prepareArgumentsForCustomCallFunc(arguments, filteredArguments, jsMethodName,
				jsFunctionAnnotation);

		verify(jsFunctionAnnotation, times(0)).name();
		Assert.assertArrayEquals("wrong result", new Object[] { null, null, arguments }, result);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#invokeManualImplementation(Method, Object...)}
	 * when current {@link JavaScriptSource} has no manual implementations.
	 * <p>
	 * As result, {@code NULL} must be returned, because no manual
	 * implementations are available.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvokeManualImplementationWithoutImplementations() throws Exception {
		System.out.println("testInvokeManualImplementationWithoutImplementations");

		Object[] arguments = new Object[] { "something", webDriver, 55 };
		Method jsMethod = getClass().getDeclaredMethod("javaScriptMethod", String.class, WebDriver.class,
				Integer.class);

		Object result = bean.invokeManualImplementation(jsMethod, arguments);

		Assert.assertNull("wrong result: ", result);
	}

	/**
	 * tests
	 * {@link JavaScriptMethodInterceptor#invokeManualImplementation(Method, Object...)}
	 * when current {@link JavaScriptSource} has manual implementations, but
	 * only one is able to handle the call.
	 * <p>
	 * As result, result of called manual implementation must be returned.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvokeManualImplementationWithImplementations() throws Exception {
		System.out.println("testInvokeManualImplementationWithImplementations");

		Object[] arguments = new Object[] { "something", webDriver, 55 };
		Method jsMethod = ManualJsInterface.class.getDeclaredMethod("javaScriptMethod", String.class, WebDriver.class,
				Integer.class);
		manualJSInterfaceImplemenations.add(new ManualJsInterface2());
		manualJSInterfaceImplemenations.add(new ManualJsInterface());

		Object result = bean.invokeManualImplementation(jsMethod, arguments);

		Assert.assertTrue("wrong result: ", (Boolean) result);
	}

	protected void javaScriptMethod() {
	}

	protected void javaScriptMethod(String param, WebDriver webDriver, Integer param2) {
	}

	protected void javaScriptMethod(String param, WebDriver webDriver, Integer param2, JQueryElement jqElement) {
	}

	protected void callFunction(@GlobalReference String reference, @JsFunctionName String functionName,
			@JsFunctionArguments Object[] functionArgs) {
	}

	protected void callFunctionWithoutAnnotatedParams(String param1, String param2, Integer param3) {
	}

	interface TestWebDriver extends WebDriver, JavascriptExecutor {
	}

	class ManualJsInterface {
		protected boolean javaScriptMethod(String param, WebDriver webDriver, Integer param2) {
			return true;
		}
	}

	class ManualJsInterface2 {
		protected void fire(String param) {
		}
	}
}
