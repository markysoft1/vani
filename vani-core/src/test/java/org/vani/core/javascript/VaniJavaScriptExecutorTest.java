package org.vani.core.javascript;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.vani.core.annotation.DetectionScript;
import org.vani.core.annotation.JavaScriptFunction;

@RunWith(MockitoJUnitRunner.class)
public class VaniJavaScriptExecutorTest {
	private VaniJavaScriptExecutor bean;

	@Mock
	private JavascriptExecutor jsExecutor;
	@Mock
	private JavaScriptSource<?> jsSource;

	@Mock
	private DetectionScript detectionScript;
	@Mock
	private JavaScriptSource jsDependency;
	@Mock
	private JavaScriptSource jsDependency2;
	@Mock
	private JavaScriptSource jsPlugin;
	@Mock
	private JavaScriptSource jsPlugin2;
	@Mock
	private JavaScriptSource jsPlugin3;
	@Mock
	private JavaScriptCallFunction jsCallFunction;
	@Mock
	private JavaScriptFunction jsFunction;

	@Before
	public void setUp() {
		bean = new VaniJavaScriptExecutor(jsExecutor, jsSource);

	}

	/**
	 * tests {@link VaniJavaScriptExecutor#mustInject(JavaScriptSource)} when
	 * source doesn't declare a detection script.
	 * <p>
	 * As result, true must be returned and no detection script should be
	 * executed.
	 * </p>
	 */
	@Test
	public void testMustInjectWithoutDectectionScript() {
		System.out.println("testMustInjectWithoutDectectionScript");

		boolean result = bean.mustInject(jsSource);

		verify(jsExecutor, times(0)).executeScript(anyObject(), anyObject());
		Assert.assertTrue("wrong result: ", result);
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#mustInject(JavaScriptSource)} when
	 * source has empty detection script.
	 * <p>
	 * As result, true must be returned and no detection script should be
	 * executed.
	 * </p>
	 */
	@Test
	public void testMustInjectWithEmptyDectectionScript() {
		System.out.println("testMustInjectWithEmptyDectectionScript");

		when(jsSource.getDetectionScriptAnnotation()).thenReturn(detectionScript);
		when(detectionScript.value()).thenReturn("");

		boolean result = bean.mustInject(jsSource);

		verify(jsExecutor, times(0)).executeScript(anyObject(), anyObject());
		Assert.assertTrue("wrong result: ", result);
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#mustInject(JavaScriptSource)} when
	 * source has detection script and it returns true.
	 * <p>
	 * As result, false must be returned, because detection script returns true
	 * so script must not be injected.
	 * </p>
	 */
	@Test
	public void testMustInjectWithDectectionScriptFalse() {
		System.out.println("testMustInjectWithDectectionScriptFalse");

		String expectedScript = "return window.vani !== undefined;";
		when(jsSource.getDetectionScriptAnnotation()).thenReturn(detectionScript);
		when(detectionScript.value()).thenReturn("window.vani !== undefined");
		when(detectionScript.autoReturn()).thenReturn(true);
		when(jsExecutor.executeScript(anyString())).thenReturn(true);

		boolean result = bean.mustInject(jsSource);

		verify(jsExecutor, times(1)).executeScript(eq(expectedScript));
		Assert.assertFalse("wrong result! ", result);
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#mustInject(JavaScriptSource)} when
	 * source has detection script and it returns false.
	 * <p>
	 * As result, true must be returned, because detection script returns false
	 * so script must be injected.
	 * </p>
	 */
	@Test
	public void testMustInjectWithDectectionScript() {
		System.out.println("testMustInjectWithDectectionScript");

		String expectedScript = "return window.vani !== undefined;";
		when(jsSource.getDetectionScriptAnnotation()).thenReturn(detectionScript);
		when(detectionScript.autoReturn()).thenReturn(true);
		when(detectionScript.value()).thenReturn("window.vani !== undefined");
		when(jsExecutor.executeScript(anyString())).thenReturn(false);

		boolean result = bean.mustInject(jsSource);

		verify(jsExecutor, times(1)).executeScript(eq(expectedScript));
		Assert.assertTrue("wrong result: ", result);
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#mustInject(JavaScriptSource)} when
	 * source has detection script without auto return.
	 * <p>
	 * As result, true must be returned and the scirpt provided by annotation
	 * must be the same as the executed one.
	 * </p>
	 */
	@Test
	public void testMustInjectWithDectectionScriptAutoReturnFalse() {
		System.out.println("testMustInjectWithDectectionScriptAutoReturnFalse");

		String expectedScript = "return window.vani !== undefined;";
		when(jsSource.getDetectionScriptAnnotation()).thenReturn(detectionScript);
		when(detectionScript.value()).thenReturn(expectedScript);
		when(jsExecutor.executeScript(anyString())).thenReturn(false);

		boolean result = bean.mustInject(jsSource);

		verify(jsExecutor, times(1)).executeScript(eq(expectedScript));
		Assert.assertTrue("wrong result: ", result);
	}

	/**
	 * tests
	 * {@link VaniJavaScriptExecutor#appendSource(StringBuilder, JavaScriptSource, Set)}
	 * when {@code NULL} as {@code jsSource} is provided.
	 * <p>
	 * As result, provided {@code stringBuilder} and {@code processingSource}
	 * must be empty.
	 * </p>
	 */
	@Test
	public void testAppendSourceWithoutJsSource() {
		System.out.println("testAppendSourceWithoutJsSource");

		StringBuilder builder = new StringBuilder();
		Set<JavaScriptSource<?>> processingSources = new HashSet<>();

		bean.appendSource(builder, null, processingSources);

		Assert.assertEquals("wrong source: ", "", builder.toString());
		Assert.assertTrue("processing sources must be empty!", processingSources.isEmpty());
	}

	/**
	 * tests
	 * {@link VaniJavaScriptExecutor#appendSource(StringBuilder, JavaScriptSource, Set)}
	 * when {@code NULL} as {@code jsSource} is provided.
	 * <p>
	 * As result, provided {@code stringBuilder} and {@code processingSource}
	 * must be empty.
	 * </p>
	 */
	@Test
	public void testAppendSourceWithProcessedJsSource() {
		System.out.println("testAppendSourceWithProcessedJsSource");

		StringBuilder builder = new StringBuilder();
		Set<JavaScriptSource<?>> processingSources = new HashSet<>();
		processingSources.add(jsSource);

		bean.appendSource(builder, jsSource, processingSources);

		Assert.assertEquals("wrong source: ", "", builder.toString());
		Assert.assertEquals("wrong count of processing sources: ", 1, processingSources.size());
		verify(jsSource, times(0)).getDependencies();
	}

	/**
	 * tests
	 * {@link VaniJavaScriptExecutor#appendSource(StringBuilder, JavaScriptSource, Set)}
	 * with jsSource with dependency and plugin.
	 * <p>
	 * As result, provided {@code stringBuilder} must contains source code of
	 * dependency, jsInterface and plugin.
	 * </p>
	 */
	@Test
	public void testAppendSource() {
		System.out.println("testAppendSource");

		String sourceCode = "source code of the js-interface;";
		String sourceCodeDependency = "source code of a dependency of the js-interface;";
		String sourceCodePlugin = "source code of a plugin for the js-interface;";
		String expectedSource = sourceCodeDependency + "\n" + sourceCode + "\n" + sourceCodePlugin + "\n";
		StringBuilder builder = new StringBuilder();
		Set<JavaScriptSource<?>> processingSources = new HashSet<>();
		when(jsSource.getDependencies()).thenReturn(Arrays.asList(jsDependency));
		when(jsSource.getPlugins()).thenReturn(Arrays.asList(jsPlugin));
		when(jsSource.getSource()).thenReturn(sourceCode);
		when(jsPlugin.getSource()).thenReturn(sourceCodePlugin);
		when(jsDependency.getSource()).thenReturn(sourceCodeDependency);

		bean.appendSource(builder, jsSource, processingSources);

		Assert.assertEquals("wrong source: ", expectedSource, builder.toString());
		Assert.assertEquals("wrong count of processing sources: ", 3, processingSources.size());
		verify(jsSource, times(1)).getDependencies();
	}

	/**
	 * tests
	 * {@link VaniJavaScriptExecutor#appendSource(StringBuilder, JavaScriptSource, Set)}
	 * with jsSource without dependency and plugin.
	 * <p>
	 * As result, provided {@code stringBuilder} must contains source code of
	 * jsInterface.
	 * </p>
	 */
	@Test
	public void testAppendSourceWithoutDependencyAndPlugin() {
		System.out.println("testAppendSourceWithoutDependencyAndPlugin");

		String sourceCode = "source code of the js-interface;";
		String expectedSource = sourceCode + "\n";
		StringBuilder builder = new StringBuilder();
		Set<JavaScriptSource<?>> processingSources = new HashSet<>();
		when(jsSource.getDependencies()).thenReturn(Arrays.asList());
		when(jsSource.getPlugins()).thenReturn(Arrays.asList());
		when(jsSource.getSource()).thenReturn(sourceCode);

		bean.appendSource(builder, jsSource, processingSources);

		Assert.assertEquals("wrong source: ", expectedSource, builder.toString());
		Assert.assertEquals("wrong count of processing sources: ", 1, processingSources.size());
		verify(jsSource, times(1)).getDependencies();
		verify(jsSource, times(1)).getPlugins();
	}

	/**
	 * tests
	 * {@link VaniJavaScriptExecutor#appendSource(StringBuilder, JavaScriptSource, Set)}
	 * with jsSource with dependencies and plugin. There is also a dependency
	 * with own dependency and plugins.
	 * <p>
	 * As result, provided {@code stringBuilder} must contains source code of
	 * dependency inclusive injecting dependencies and plugins, jsInterface and
	 * plugin.
	 * </p>
	 */
	@Test
	public void testAppendSourceWithDependenciesWithPlugins() {
		System.out.println("testAppendSourceWithDependenciesWithPlugins");

		String sourceCode = "source code of the js-interface;";
		String sourceCodeDependency = "source code of a dependency of the js-interface;";
		String sourceCodePlugin = "source code of a plugin for the js-interface;";
		String sourceCodePlugin3 = "source code of a plugin for the dependency of js-interface;";
		String expectedSource = sourceCodeDependency + "\n" + sourceCodePlugin3 + "\n" + sourceCode + "\n"
				+ sourceCodePlugin + "\n";
		StringBuilder builder = new StringBuilder();
		Set<JavaScriptSource<?>> processingSources = new HashSet<>();
		when(jsSource.getDependencies()).thenReturn(Arrays.asList(jsDependency, jsDependency2));
		when(jsSource.getPlugins()).thenReturn(Arrays.asList(jsPlugin));
		when(jsSource.getSource()).thenReturn(sourceCode);
		when(jsPlugin.getSource()).thenReturn(sourceCodePlugin);
		when(jsPlugin2.getDetectionScriptAnnotation()).thenReturn(detectionScript);
		when(jsPlugin3.getSource()).thenReturn(sourceCodePlugin3);
		when(jsDependency.getSource()).thenReturn(sourceCodeDependency);
		when(jsDependency2.getDependencies()).thenReturn(Arrays.asList(jsDependency));
		when(jsDependency2.getDetectionScriptAnnotation()).thenReturn(detectionScript);
		when(jsDependency2.getPlugins()).thenReturn(Arrays.asList(jsPlugin3, jsPlugin2));
		when(detectionScript.value()).thenReturn("window.vani !== undefined");
		when(jsExecutor.executeScript(anyString())).thenReturn(true);

		bean.appendSource(builder, jsSource, processingSources);

		Assert.assertEquals("wrong source: ", expectedSource, builder.toString());
		Assert.assertEquals("wrong count of processing sources: ", 6, processingSources.size());
		verify(jsSource, times(1)).getDependencies();
		verify(jsDependency2, times(1)).getDependencies();
		verify(jsDependency2, times(0)).getSource();
		verify(jsPlugin2, times(0)).getSource();
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#prepareSource(String)} with provided
	 * function code is an empty literal.
	 * <p>
	 * As result, only the source code of the interface should be returned.
	 * </p>
	 */
	@Test
	public void testPrepareSourceWithEmptyFunctionCode() {
		System.out.println("testPrepareSourceWithEmptyFunctionCode");

		String sourceCode = "source code of the js-interface;";
		String expectedSource = sourceCode + "\n";
		when(jsSource.getDependencies()).thenReturn(Arrays.asList());
		when(jsSource.getPlugins()).thenReturn(Arrays.asList());
		when(jsSource.getSource()).thenReturn(sourceCode);

		String result = bean.prepareSource("");

		Assert.assertEquals("wrong source: ", expectedSource, result);
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#prepareSource(String)} with provided
	 * function code is not an empty literal.
	 * <p>
	 * As result, the source code of the interface and provided function code
	 * should be returned.
	 * </p>
	 */
	@Test
	public void testPrepareSource() {
		System.out.println("testPrepareSource");

		String sourceCode = "source code of the js-interface;";
		String expectedSource = sourceCode + "\n";
		when(jsSource.getDependencies()).thenReturn(Arrays.asList());
		when(jsSource.getPlugins()).thenReturn(Arrays.asList());
		when(jsSource.getSource()).thenReturn(sourceCode);

		String result = bean.prepareSource("showSecret();");

		Assert.assertEquals("wrong source: ", expectedSource + "showSecret();", result);
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#handleResult(Object)} with
	 * {@code NULL} as argument.
	 * <p>
	 * As result, {@code NULL} should be returned, because it was specified as
	 * parameter.
	 * </p>
	 */
	@Test
	public void testHandleResultWithNull() {
		System.out.println("testHandleResultWithNull");

		Object result = bean.handleResult(null);

		Assert.assertNull("result must be NULL, because NULL was specified!", result);
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#handleResult(Object)} with
	 * {@code String} as argument.
	 * <p>
	 * As result, provided string should be returned.
	 * </p>
	 */
	@Test
	public void testHandleResultWithString() {
		System.out.println("testHandleResultWithString");

		String expected = "secret key";
		Object result = bean.handleResult(expected);

		Assert.assertEquals("wrong result: ", expected, result);
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#handleResult(Object)} with
	 * {@code Integer} as argument.
	 * <p>
	 * As result, provided integer should be returned.
	 * </p>
	 */
	@Test
	public void testHandleResultWithInteger() {
		System.out.println("testHandleResultWithInteger");

		int expected = 394345;
		Object result = bean.handleResult(expected);

		Assert.assertEquals("wrong result: ", expected, result);
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#handleResult(Object)} with
	 * {@code String} with error key as argument.
	 * <p>
	 * As result, a {@link JavaScriptException} must be thrown, because provided
	 * string starts with js-error key.
	 * </p>
	 */
	@Test
	public void testHandleResultWithJsErrorString() {
		System.out.println("testHandleResultWithJsErrorString");

		String expected = "@JS-ERROR:Sorry an error occurred in your script!";
		try {
			bean.handleResult(expected);
			Assert.fail("An JavaScriptException must be thrown!");
		} catch (JavaScriptException ex) {
			String message = ex.getMessage();
			Assert.assertEquals("wrong message: ", "Failed to execute injected script: " + expected, message);
		}
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#doExecuteAsync(String, Object...)}
	 * without arguments.
	 * <p>
	 * As result,
	 * {@link JavascriptExecutor#executeAsyncScript(String, Object...)} must be
	 * invoked.
	 * </p>
	 */
	@Test
	public void testDoExecuteAsyncWithoutArgs() {
		System.out.println("testDoExecuteAsyncWithoutArgs");

		String script = "alert('hello world!');";

		bean.doExecuteAsync(script);

		verify(jsExecutor, times(1)).executeAsyncScript(script);
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#doExecuteAsync(String, Object...)}
	 * with arguments.
	 * <p>
	 * As result,
	 * {@link JavascriptExecutor#executeAsyncScript(String, Object...)} must be
	 * invoked.
	 * </p>
	 */
	@Test
	public void testDoExecuteAsync() {
		System.out.println("testDoExecuteAsync");

		String script = "alert(arguments[0]);";

		bean.doExecuteAsync(script, "hello world");

		verify(jsExecutor, times(1)).executeAsyncScript(script, "hello world");
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#doExecute(String, Object...)} with
	 * arguments.
	 * <p>
	 * As result,
	 * {@link JavascriptExecutor#executeAsyncScript(String, Object...)} must be
	 * invoked.
	 * </p>
	 */
	@Test
	public void testDoExecute() {
		System.out.println("testDoExecute");

		String script = "alert(arguments[0]);";

		bean.doExecute(script, "hello world");

		verify(jsExecutor, times(1)).executeScript(script, "hello world");
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#getCallSource(String)} with default
	 * call function.
	 * <p>
	 * As result, the default call function source must be returned, because
	 * {@code jsSource} doesn't define a customer one. invoked.
	 * </p>
	 */
	@Test
	public void testGetCallSourceWithDefaultCallFunction() {
		System.out.println("testGetCallSourceWithDefaultCallFunction");

		String result = bean.getCallSource("showSecret");

		Assert.assertEquals("wrong call source: ", "return showSecret.apply(null,arguments);", result);
		verify(jsSource, times(1)).getJsCallFunction();
	}

	/**
	 * tests {@link VaniJavaScriptExecutor#getCallSource(String)} with custom
	 * call function.
	 * <p>
	 * As result, the custom call function source must be returned.
	 * </p>
	 */
	@Test
	public void testGetCallSourceWithCustomCallFunction() {
		System.out.println("testGetCallSourceWithCustomCallFunction");

		String callFunction = "function(args){alert('my secret key');}";
		String expected = "var vaniJsCallFunc_showSecret = " + callFunction
				+ ";return vaniJsCallFunc_showSecret.apply(null,arguments);";
		when(jsSource.getJsCallFunction()).thenReturn(jsCallFunction);
		when(jsCallFunction.getCallFunctionSource()).thenReturn(callFunction);

		String result = bean.getCallSource("showSecret");

		Assert.assertEquals("wrong call source: ", expected, result);
		verify(jsSource, times(2)).getJsCallFunction();
	}

	/**
	 * tests
	 * {@link VaniJavaScriptExecutor#execute(String, JavaScriptFunction, Object...)}
	 * with {@link JavaScriptFunction} without name and value.
	 * <p>
	 * As result, the provided method name must be used as function name.
	 * </p>
	 */
	@Test
	public void testExecuteWithoutValueAndName() {
		System.out.println("testExecuteWithoutValueAndName");

		Object[] args = new Object[] { "hello world!" };
		String expected = "\ntry{return showSecret.apply(null,arguments);}catch(ex){console.log('Failed to execute injected script: '+ex);return '@JS-ERROR: '+ex;}";
		when(jsExecutor.executeScript(anyString(), anyObject())).thenReturn(true);
		when(jsSource.getSource()).thenReturn("");

		Object result = bean.execute("showSecret", jsFunction, args);

		Assert.assertTrue("wrong js return value!", (Boolean) result);
		verify(jsExecutor, times(1)).executeScript(expected, args);
		verify(jsSource, times(1)).getJsCallFunction();
		verify(jsSource, times(1)).getDependencies();
	}

	/**
	 * tests
	 * {@link VaniJavaScriptExecutor#execute(String, JavaScriptFunction, Object...)}
	 * with {@link JavaScriptFunction} with name.
	 * <p>
	 * As result, the provided name must be used as function name.
	 * </p>
	 */
	@Test
	public void testExecuteWithName() {
		System.out.println("testExecuteWithName");

		Object[] args = new Object[] { "hello world!" };
		String expected = "\ntry{return window.vani.showSecret.apply(null,arguments);}catch(ex){console.log('Failed to execute injected script: '+ex);return '@JS-ERROR: '+ex;}";
		when(jsExecutor.executeScript(anyString(), anyObject())).thenReturn(true);
		when(jsSource.getSource()).thenReturn("");
		when(jsFunction.name()).thenReturn("window.vani.showSecret");

		Object result = bean.execute("showSecret", jsFunction, args);

		Assert.assertTrue("wrong js return value!", (Boolean) result);
		verify(jsExecutor, times(1)).executeScript(expected, args);
		verify(jsSource, times(1)).getJsCallFunction();
		verify(jsSource, times(1)).getDependencies();
	}

	/**
	 * tests
	 * {@link VaniJavaScriptExecutor#execute(String, JavaScriptFunction, Object...)}
	 * with {@link JavaScriptFunction} with name.
	 * <p>
	 * As result, the provided name must be used as function name.
	 * </p>
	 */
	@Test
	public void testExecuteWithValue() {
		System.out.println("testExecuteWithValue");

		Object[] args = new Object[] { "hello world!" };
		String expected = "\ntry{alert(arguments[0]);}catch(ex){console.log('Failed to execute injected script: '+ex);return '@JS-ERROR: '+ex;}";
		when(jsExecutor.executeScript(anyString(), anyObject())).thenReturn(true);
		when(jsSource.getSource()).thenReturn("");
		when(jsFunction.value()).thenReturn("alert(arguments[0]);");

		Object result = bean.execute("showSecret", jsFunction, args);

		Assert.assertTrue("wrong js return value!", (Boolean) result);
		verify(jsExecutor, times(1)).executeScript(expected, args);
		verify(jsSource, times(0)).getJsCallFunction();
		verify(jsSource, times(1)).getDependencies();
	}

	/**
	 * tests
	 * {@link VaniJavaScriptExecutor#execute(String, JavaScriptFunction, Object...)}
	 * with {@link JavaScriptFunction} when js-error key is returned.
	 * <p>
	 * As result, {@link JavaScriptException} must be thrown.
	 * </p>
	 */
	@Test(expected = JavaScriptException.class)
	public void testExecuteWithJsError() {
		System.out.println("testExecuteWithJsError");

		Object[] args = new Object[] { "hello world!" };
		when(jsExecutor.executeScript(anyString(), anyObject()))
				.thenReturn("@JS-ERROR:Sorry, it is not allowed to show secret!");
		when(jsSource.getSource()).thenReturn("");
		when(jsFunction.value()).thenReturn("alert(arguments[0]);");
		bean.execute("showSecret", jsFunction, args);
	}
}
