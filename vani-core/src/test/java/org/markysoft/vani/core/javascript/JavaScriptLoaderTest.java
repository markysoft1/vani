package org.markysoft.vani.core.javascript;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.DetectionScript;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JsCallFunction;
import org.markysoft.vani.core.annotation.JsFunctionArguments;
import org.markysoft.vani.core.annotation.JsFunctionName;
import org.markysoft.vani.core.util.VaniReflectionUtil;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.reflections.Reflections;

@RunWith(MockitoJUnitRunner.class)
public class JavaScriptLoaderTest {
	private JavaScriptLoader bean;

	@Mock
	private VaniReflectionUtil reflectionUtil;

	@Mock
	private VaniContext vaniContext;
	@Mock
	private Reflections reflections;
	@Mock
	private JavaScriptSource jsSource;
	@Mock
	private JavaScript javaScript;

	@Captor
	private ArgumentCaptor<Pattern> captorPattern;
	@Captor
	private ArgumentCaptor<JavaScriptCallFunction> captorJsCallFunction;

	private VaniReflectionUtil realReflectionUtil = new VaniReflectionUtil();

	@Before
	public void setUp() {
		bean = new JavaScriptLoader();
		bean.setReflectionUtil(reflectionUtil);

		when(vaniContext.getReflections()).thenReturn(reflections);
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#getResourcesPathFromClasspath(String, VaniContext)}
	 * with classpath without path and without matching
	 * <p>
	 * As result, empty set must be returned.
	 * </p>
	 */
	@Test
	public void testGetResourcesPathFromClasspathWithoutPathAndMatching() {
		System.out.println("testGetResourcesPathFromClasspathWithoutPathAndMatching");
		String classpath = "secret\\.js";

		when(reflections.getResources((Pattern) anyObject())).thenReturn(new HashSet<>());

		Set<String> result = bean.getResourcesPathFromClasspath(classpath, vaniContext);

		Assert.assertTrue("empty set must be returned because nothing should be found!", result.isEmpty());
		verify(reflections, times(1)).getResources(captorPattern.capture());
		Assert.assertEquals("wrong pattern found: ", classpath, captorPattern.getValue().pattern());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#getResourcesPathFromClasspath(String, VaniContext)}
	 * with classpath without path. The lookup returns some matches
	 * <p>
	 * As result, all matches must be returned regardless of it' path.
	 * </p>
	 */
	@Test
	public void testGetResourcesPathFromClasspathWithoutPath() {
		System.out.println("testGetResourcesPathFromClasspathWithoutPath");
		String classpath = "secret\\.js";

		Set<String> matches = new HashSet<>(
				Arrays.asList("at/marriage/web/secret.js", "com/ran/some/secret.js", "com/ran/some/ware/secret.js"));
		when(reflections.getResources((Pattern) anyObject())).thenReturn(matches);

		Set<String> result = bean.getResourcesPathFromClasspath(classpath, vaniContext);

		Assert.assertArrayEquals("wrong resources: ", matches.toArray(), result.toArray());
		verify(reflections, times(1)).getResources(captorPattern.capture());
		Assert.assertEquals("wrong pattern found: ", classpath, captorPattern.getValue().pattern());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#getResourcesPathFromClasspath(String, VaniContext)}
	 * with classpath containing path (absolute path). The lookup returns some
	 * matches
	 * <p>
	 * As result, all only matches must be returned, which path is equal to
	 * provided.
	 * </p>
	 */
	@Test
	public void testGetResourcesPathFromClasspathWithPath() {
		System.out.println("testGetResourcesPathFromClasspathWithPath");
		String classpath = "com/ran/some/secret\\.js";

		Set<String> matches = new HashSet<>(
				Arrays.asList("at/marriage/web/secret.js", "com/ran/some/secret.js", "com/ran/some/ware/secret.js"));
		List<String> expected = new ArrayList<>(Arrays.asList("com/ran/some/secret.js"));
		when(reflections.getResources((Pattern) anyObject())).thenReturn(matches);

		Set<String> result = bean.getResourcesPathFromClasspath(classpath, vaniContext);

		Assert.assertArrayEquals("wrong resources: ", expected.toArray(), result.toArray());
		verify(reflections, times(1)).getResources(captorPattern.capture());
		Assert.assertEquals("wrong pattern found: ", "secret\\.js", captorPattern.getValue().pattern());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#getResourcesPathFromClasspath(String, VaniContext)}
	 * with classpath containing path (path with wildcard). The lookup returns
	 * some matches
	 * <p>
	 * As result, all only matches must be returned, which path is matches to
	 * provided.
	 * </p>
	 */
	@Test
	public void testGetResourcesPathFromClasspathWithWildcardPath() {
		System.out.println("testGetResourcesPathFromClasspathWithWildcardPath");
		String classpath = "com/ran/some.*/secret\\.js";

		Set<String> matches = new HashSet<>(
				Arrays.asList("at/marriage/web/secret.js", "com/ran/some/secret.js", "com/ran/some/ware/secret.js"));
		List<String> expected = new ArrayList<>(Arrays.asList("com/ran/some/secret.js", "com/ran/some/ware/secret.js"));
		when(reflections.getResources((Pattern) anyObject())).thenReturn(matches);

		Set<String> result = bean.getResourcesPathFromClasspath(classpath, vaniContext);

		Assert.assertArrayEquals("wrong resources: ", expected.toArray(), result.toArray());
		verify(reflections, times(1)).getResources(captorPattern.capture());
		Assert.assertEquals("wrong pattern found: ", "secret\\.js", captorPattern.getValue().pattern());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#testGetResourcesPathFromFilesystemWithoutPath(String)}
	 * with resourcePath without path, but requested file exists in current
	 * directory.
	 * <p>
	 * As result, requested resource must be returned, because it is contained
	 * in current directory.
	 * </p>
	 * 
	 */
	@Test
	public void testGetResourcesPathFromFilesystemWithoutPath() throws Throwable {
		System.out.println("testGetResourcesPathFromFilesystemWithoutPath");
		String path = "pom.xml";

		List<Path> result = bean.getResourcesPathFromFilesystem(path);

		Assert.assertFalse("resource must be found!", result.isEmpty());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#testGetResourcesPathFromFilesystemWithoutPath(String)}
	 * with resourcePath with path (absolute path).
	 * <p>
	 * As result, requested resource must be returned.
	 * </p>
	 * 
	 */
	@Test
	public void testGetResourcesPathFromFilesystemWithPath() throws Throwable {
		System.out.println("testGetResourcesPathFromFilesystemWithoutPath");
		String path = "src/main/resources/org/markysoft/vani/javascript/vani-utils.js";

		List<Path> result = bean.getResourcesPathFromFilesystem(path);

		Assert.assertFalse("resource must be found!", result.isEmpty());
	}

	/**
	 * tests {@link JavaScriptLoader#fromFilesystem(String, VaniContext)} with
	 * valid resourcePath.
	 * <p>
	 * As result, content of requested resource must be returned.
	 * </p>
	 */
	@Test
	public void testFromFilesystem() throws Throwable {
		System.out.println("testFromFilesystem");
		String path = "pom.xml";

		String expected = FileUtils.readFileToString(new File("./pom.xml"));

		StringBuilder result = bean.fromFilesystem(path, vaniContext);

		Assert.assertEquals("wrong resouce content found: ", expected, result.toString().trim());
	}

	/**
	 * tests {@link JavaScriptLoader#fromFilesystem(String, VaniContext)} with
	 * unknown resourcePath.
	 * <p>
	 * As result, empty {@link StringBuilder} must be returned, because
	 * requested resource does not exist.
	 * </p>
	 */
	@Test
	public void testFromFilesystemWithUnknown() throws Throwable {
		System.out.println("testFromFilesystemWithUnknown");
		String path = "secret.xml";

		StringBuilder result = bean.fromFilesystem(path, vaniContext);

		Assert.assertNotNull("result must not be NULL, because empty StringBuilder will be used as fallback!", result);
		Assert.assertEquals("wrong result: ", "", result.toString());
	}

	/**
	 * tests {@link JavaScriptLoader#fromClasspath(String, VaniContext)} with
	 * unknown resource.
	 * <p>
	 * As result, empty {@link StringBuilder} must be returned, because
	 * requested resource does not exist.
	 * </p>
	 */
	@Test
	public void testFromClasspathWithUnknown() throws Throwable {
		System.out.println("testFromClasspathWithUnknown");
		String path = "secret.js";

		when(reflections.getResources((Pattern) anyObject())).thenReturn(new HashSet<String>());

		StringBuilder result = bean.fromClasspath(path, vaniContext);

		verify(vaniContext, times(1)).getReflections();
		verify(reflections, times(1)).getResources((Pattern) anyObject());
		Assert.assertNotNull("result must not be NULL, because empty StringBuilder will be used as fallback!", result);
		Assert.assertEquals("wrong result: ", "", result.toString());
	}

	/**
	 * tests {@link JavaScriptLoader#fromClasspath(String, VaniContext)} with
	 * valid resource path.
	 * <p>
	 * As result, correct content must be returned.
	 * </p>
	 */
	@Test
	public void testFromClasspath() throws Throwable {
		System.out.println("testFromFilesystemWithUnknown");
		String path = "vani-utils.js";

		String expected = FileUtils
				.readFileToString(new File("src/main/resources/org/markysoft/vani/javascript/vani-utils.js"));
		Set<String> matches = new HashSet<>(Arrays.asList("org/markysoft/vani/javascript/vani-utils.js"));
		when(reflections.getResources((Pattern) anyObject())).thenReturn(matches);

		StringBuilder result = bean.fromClasspath(path, vaniContext);

		verify(vaniContext, times(1)).getReflections();
		verify(reflections, times(1)).getResources((Pattern) anyObject());
		Assert.assertNotNull("result must not be NULL, because empty StringBuilder will be used as fallback!", result);
		Assert.assertEquals("wrong result: ", expected, result.toString().trim());
	}

	/**
	 * tests {@link JavaScriptLoader#fromClasspath(String, VaniContext)} with
	 * valid resource path. The resource path will starts with '/'.
	 * <p>
	 * As result, correct content must be returned.
	 * </p>
	 */
	@Test
	public void testFromClasspathWithLeadingSlash() throws Throwable {
		System.out.println("testFromClasspathWithLeadingSlash");
		String path = "vani-utils.js";

		String expected = FileUtils
				.readFileToString(new File("src/main/resources/org/markysoft/vani/javascript/vani-utils.js"));
		Set<String> matches = new HashSet<>(Arrays.asList("/org/markysoft/vani/javascript/vani-utils.js"));
		when(reflections.getResources((Pattern) anyObject())).thenReturn(matches);

		StringBuilder result = bean.fromClasspath(path, vaniContext);

		verify(vaniContext, times(1)).getReflections();
		verify(reflections, times(1)).getResources((Pattern) anyObject());
		Assert.assertNotNull("result must not be NULL, because empty StringBuilder will be used as fallback!", result);
		Assert.assertEquals("wrong result: ", expected, result.toString().trim());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#setCallFunction(JavaScriptSource, VaniContext)}
	 * without custom call function.
	 * <p>
	 * As result, nothing should be done, because no call function is declared.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetCallFunctionWithout() throws Throwable {
		System.out.println("testSetCallFunctionWithout");

		when(jsSource.getJsInterface()).thenReturn(JSInterface.class);

		bean.setCallFunction(jsSource, vaniContext);

		verify(reflectionUtil, times(1)).getAnnotatedMethodWith(JSInterface.class, JsCallFunction.class, null);
		verify(jsSource, times(0)).setJsCallFunction(anyObject());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#setCallFunction(JavaScriptSource, VaniContext)}
	 * with custom call function, but without path.
	 * <p>
	 * As result, nothing should be done, because no valid call function is
	 * declared.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetCallFunctionWithEmptyPath() throws Throwable {
		System.out.println("testSetCallFunctionWithEmptyPath");

		Class<?> jsInterfaceClass = JSInterfaceWithoutCallFunctionPath.class;
		Method method = realReflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null);
		when(jsSource.getJsInterface()).thenReturn(jsInterfaceClass);
		when(reflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null)).thenReturn(method);

		bean.setCallFunction(jsSource, vaniContext);

		verify(reflectionUtil, times(1)).getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null);
		verify(jsSource, times(0)).setJsCallFunction(anyObject());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#setCallFunction(JavaScriptSource, VaniContext)}
	 * with custom call function, but specified classpath does not exist.
	 * <p>
	 * As result, nothing should be done, because no call function is available.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetCallFunctionWithClasspathOfUnknownResource() throws Throwable {
		System.out.println("testSetCallFunctionWithClasspathOfUnknownResource");

		Class<?> jsInterfaceClass = JSInterfaceWithCallFunctionClasspathNotExist.class;
		Method method = realReflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null);
		when(jsSource.getJsInterface()).thenReturn(jsInterfaceClass);
		when(reflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null)).thenReturn(method);
		when(reflections.getResources((Pattern) anyObject())).thenReturn(new HashSet<String>());

		bean.setCallFunction(jsSource, vaniContext);

		verify(reflectionUtil, times(1)).getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null);
		verify(jsSource, times(0)).setJsCallFunction(anyObject());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#setCallFunction(JavaScriptSource, VaniContext)}
	 * with custom call function defined by classpath.
	 * <p>
	 * As result, referenced call function must be loaded and set to provided
	 * {@link JavaScriptSource}.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetCallFunctionWithClasspath() throws Throwable {
		System.out.println("testSetCallFunctionWithClasspath");

		Class<?> jsInterfaceClass = JSInterfaceWithCallFunctionClasspath.class;
		Method method = realReflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null);
		JsCallFunction expectedCallAnnotation = method.getAnnotation(JsCallFunction.class);
		when(jsSource.getJsInterface()).thenReturn(jsInterfaceClass);
		when(reflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null)).thenReturn(method);
		Set<String> matches = new HashSet<>(Arrays.asList("org/markysoft/vani/javascript/vani-jquery-call.js"));
		when(reflections.getResources((Pattern) anyObject())).thenReturn(matches);
		String expectedSource = FileUtils
				.readFileToString(new File("src/main/resources/org/markysoft/vani/javascript/vani-jquery-call.js"));

		bean.setCallFunction(jsSource, vaniContext);

		verify(reflectionUtil, times(1)).getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null);
		verify(jsSource, times(1)).setJsCallFunction(captorJsCallFunction.capture());
		JavaScriptCallFunction actual = captorJsCallFunction.getValue();
		Assert.assertEquals("wrong callMehtod: ", method, actual.getCallMethod());
		Assert.assertEquals("wrong source: ", expectedSource, actual.getCallFunctionSource().trim());
		Assert.assertEquals("wrong annotation: ", expectedCallAnnotation, actual.getJsCallFunctionAnnotation());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#setCallFunction(JavaScriptSource, VaniContext)}
	 * with custom call function, but specified filesystem path does not exist.
	 * <p>
	 * As result, nothing should be done, because no call function is available.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetCallFunctionWithFilesystemOfUnknownResource() throws Throwable {
		System.out.println("testSetCallFunctionWithFilesystemOfUnknownResource");

		Class<?> jsInterfaceClass = JSInterfaceWithCallFunctionFilesystemNotExist.class;
		Method method = realReflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null);
		when(jsSource.getJsInterface()).thenReturn(jsInterfaceClass);
		when(reflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null)).thenReturn(method);

		bean.setCallFunction(jsSource, vaniContext);

		verify(reflectionUtil, times(1)).getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null);
		verify(jsSource, times(0)).setJsCallFunction(anyObject());
	}

	/**
	 * tests
	 * {@link JavaScriptLoader#setCallFunction(JavaScriptSource, VaniContext)}
	 * with custom call function defined by filesystem path.
	 * <p>
	 * As result, referenced call function must be loaded and set to provided
	 * {@link JavaScriptSource}.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetCallFunctionWithFilesystem() throws Throwable {
		System.out.println("testSetCallFunctionWithFilesystem");

		Class<?> jsInterfaceClass = JSInterfaceWithCallFunctionFilesystem.class;
		Method method = realReflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null);
		JsCallFunction expectedCallAnnotation = method.getAnnotation(JsCallFunction.class);
		when(jsSource.getJsInterface()).thenReturn(jsInterfaceClass);
		when(reflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null)).thenReturn(method);
		String expectedSource = FileUtils.readFileToString(new File("./pom.xml"));

		bean.setCallFunction(jsSource, vaniContext);

		verify(reflectionUtil, times(1)).getAnnotatedMethodWith(jsInterfaceClass, JsCallFunction.class, null);
		verify(jsSource, times(1)).setJsCallFunction(captorJsCallFunction.capture());
		JavaScriptCallFunction actual = captorJsCallFunction.getValue();
		Assert.assertEquals("wrong callMehtod: ", method, actual.getCallMethod());
		Assert.assertEquals("wrong source: ", expectedSource, actual.getCallFunctionSource().trim());
		Assert.assertEquals("wrong annotation: ", expectedCallAnnotation, actual.getJsCallFunctionAnnotation());
	}

	@JavaScript
	interface JSInterface {
	}

	@JavaScript
	interface JSInterfaceWithoutCallFunctionPath {
		@JsCallFunction("")
		public String call(@JsFunctionName String functionName, @JsFunctionArguments Object... args);
	}

	@JavaScript
	interface JSInterfaceWithCallFunctionClasspathNotExist {
		@JsCallFunction("classpath:secret.js")
		public String call(@JsFunctionName String functionName, @JsFunctionArguments Object... args);
	}

	@JavaScript
	interface JSInterfaceWithCallFunctionClasspath {
		@JsCallFunction("classpath:vani-jquery-call.js")
		public String call(@JsFunctionName String functionName, @JsFunctionArguments Object... args);
	}

	@JavaScript
	interface JSInterfaceWithCallFunctionFilesystemNotExist {
		@JsCallFunction("secret.js")
		public String call(@JsFunctionName String functionName, @JsFunctionArguments Object... args);
	}

	@JavaScript
	interface JSInterfaceWithCallFunctionFilesystem {
		@JsCallFunction("pom.xml")
		public String call(@JsFunctionName String functionName, @JsFunctionArguments Object... args);
	}

	/**
	 * tests {@link JavaScriptLoader#setDetectedMethod(JavaScriptSource)}
	 * without declaring one.
	 * <p>
	 * As result, nothing should be done, because there is no method for
	 * detection whether javascript source must be injected into page.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetDetectedMethodWithout() throws Throwable {
		System.out.println("testSetDetectedMethodWithout");

		Class<?> jsInterfaceClass = JSInterface.class;
		when(jsSource.getJsInterface()).thenReturn(jsInterfaceClass);

		bean.setDetectedMethod(jsSource);

		verify(reflectionUtil, times(1)).getAnnotatedMethodWith(jsInterfaceClass, DetectionScript.class, null);
		verify(jsSource, times(0)).setDetectionScriptAnnotation(anyObject());
	}

	/**
	 * tests {@link JavaScriptLoader#setDetectedMethod(JavaScriptSource)} when
	 * detection script is declared.
	 * <p>
	 * As result, declared {@link DetectionScript} annotation must be set to
	 * provided {@link JavaScriptSource}.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetDetectedMethod() throws Throwable {
		System.out.println("testSetDetectedMethod");

		Class<?> jsInterfaceClass = JSInterfaceWithDetectionScript.class;
		when(jsSource.getJsInterface()).thenReturn(jsInterfaceClass);
		Method method = realReflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, DetectionScript.class, null);
		DetectionScript expectedDetectionAnnotation = method.getAnnotation(DetectionScript.class);
		when(jsSource.getJsInterface()).thenReturn(jsInterfaceClass);
		when(reflectionUtil.getAnnotatedMethodWith(jsInterfaceClass, DetectionScript.class, null)).thenReturn(method);

		bean.setDetectedMethod(jsSource);

		verify(reflectionUtil, times(1)).getAnnotatedMethodWith(jsInterfaceClass, DetectionScript.class, null);
		verify(jsSource, times(1)).setDetectionScriptAnnotation(expectedDetectionAnnotation);
	}

	@JavaScript
	interface JSInterfaceWithDetectionScript {
		@DetectionScript("typeof jQuery !== 'undefined'")
		public boolean isAvailable();
	}

	/**
	 * tests {@link JavaScriptLoader#load(JavaScript, Class, VaniContext)} when
	 * provided {@link JavaScript} has no sources.
	 * <p>
	 * As result, {@link JavaScriptSource} with empty source must be returned.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testLoadWithoutSources() throws Throwable {
		System.out.println("testSetDetectedMethod");

		Class<?> jsInterfaceClass = JSInterfaceWithDetectionScript.class;

		JavaScriptSource result = bean.load(javaScript, jsInterfaceClass, vaniContext);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong content: ", "", result.getSource());
		Assert.assertEquals("wrong js-interface: ", jsInterfaceClass, result.getJsInterface());
		verify(javaScript, times(1)).sources();
		verify(javaScript, times(1)).source();
	}

	/**
	 * tests {@link JavaScriptLoader#load(JavaScript, Class, VaniContext)} when
	 * provided {@link JavaScript} has empty source declaration.
	 * <p>
	 * As result, {@link JavaScriptSource} with empty source must be returned.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testLoadWithEmptySource() throws Throwable {
		System.out.println("testLoadWithEmptySource");

		Class<?> jsInterfaceClass = JSInterface.class;
		when(javaScript.source()).thenReturn("");

		JavaScriptSource result = bean.load(javaScript, jsInterfaceClass, vaniContext);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong content: ", "", result.getSource());
		Assert.assertEquals("wrong js-interface: ", jsInterfaceClass, result.getJsInterface());
		verify(javaScript, times(1)).sources();
		verify(javaScript, times(1)).source();
	}

	/**
	 * tests {@link JavaScriptLoader#load(JavaScript, Class, VaniContext)} when
	 * provided {@link JavaScript} has empty array as sources declaration.
	 * <p>
	 * As result, {@link JavaScriptSource} with empty source must be returned.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testLoadWithEmptySources() throws Throwable {
		System.out.println("testLoadWithEmptySources");

		Class<?> jsInterfaceClass = JSInterface.class;
		when(javaScript.sources()).thenReturn(new String[] {});

		JavaScriptSource result = bean.load(javaScript, jsInterfaceClass, vaniContext);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong content: ", "", result.getSource());
		Assert.assertEquals("wrong js-interface: ", jsInterfaceClass, result.getJsInterface());
		verify(javaScript, times(1)).sources();
		verify(javaScript, times(1)).source();
	}

	/**
	 * tests {@link JavaScriptLoader#load(JavaScript, Class, VaniContext)} when
	 * provided {@link JavaScript} has empty array as sources declaration.
	 * <p>
	 * As result, {@link JavaScriptSource} with empty source must be returned.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testLoadWithSourcesAndEmptyEntry() throws Throwable {
		System.out.println("testLoadWithSourcesAndEmptyEntry");

		Class<?> jsInterfaceClass = JSInterface.class;
		when(javaScript.sources()).thenReturn(new String[] { "" });

		JavaScriptSource result = bean.load(javaScript, jsInterfaceClass, vaniContext);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong content: ", "", result.getSource());
		Assert.assertEquals("wrong js-interface: ", jsInterfaceClass, result.getJsInterface());
		verify(javaScript, times(1)).sources();
		verify(javaScript, times(0)).source();
	}

	/**
	 * tests {@link JavaScriptLoader#load(JavaScript, Class, VaniContext)} when
	 * provided {@link JavaScript} has array with one filesystem path
	 * declaration.
	 * <p>
	 * As result, {@link JavaScriptSource} with content must be returned.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testLoadWithSourcesFilesystem() throws Throwable {
		System.out.println("testLoadWithSourcesFilesystem");

		String expectedContent = FileUtils.readFileToString(new File("./pom.xml"));
		Class<?> jsInterfaceClass = JSInterface.class;
		when(javaScript.sources()).thenReturn(new String[] { "pom.xml" });

		JavaScriptSource result = bean.load(javaScript, jsInterfaceClass, vaniContext);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong content: ", expectedContent, result.getSource().trim());
		Assert.assertEquals("wrong js-interface: ", jsInterfaceClass, result.getJsInterface());
		verify(javaScript, times(1)).sources();
		verify(javaScript, times(0)).source();
	}

	/**
	 * tests {@link JavaScriptLoader#load(JavaScript, Class, VaniContext)} when
	 * provided {@link JavaScript} has array with filesystem and classpath paths
	 * declaration.
	 * <p>
	 * As result, {@link JavaScriptSource} with content of both resources must
	 * be returned.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testLoadWithSourcesFilesystemAndClasspath() throws Throwable {
		System.out.println("testLoadWithSourcesFilesystemAndClasspath");

		String expectedContent = FileUtils.readFileToString(new File("./pom.xml"));
		expectedContent += "\n\n" + FileUtils
				.readFileToString(new File("src/main/resources/org/markysoft/vani/javascript/vani-utils.js"));
		Class<?> jsInterfaceClass = JSInterface.class;
		when(javaScript.sources()).thenReturn(new String[] { "pom.xml", "classpath:vani-utils.js" });
		Set<String> matches = new HashSet<>(Arrays.asList("org/markysoft/vani/javascript/vani-utils.js"));
		when(reflections.getResources((Pattern) anyObject())).thenReturn(matches);

		JavaScriptSource result = bean.load(javaScript, jsInterfaceClass, vaniContext);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong content: ", expectedContent, result.getSource().trim());
		Assert.assertEquals("wrong js-interface: ", jsInterfaceClass, result.getJsInterface());
		verify(javaScript, times(1)).sources();
		verify(javaScript, times(0)).source();
	}

	/**
	 * tests {@link JavaScriptLoader#load(JavaScript, Class, VaniContext)} when
	 * provided {@link JavaScript} has filesystem path declaration.
	 * <p>
	 * As result, {@link JavaScriptSource} with content of declared resource
	 * must be returned.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testLoadWithSourceFilesystem() throws Throwable {
		System.out.println("testLoadWithSourceFilesystem");

		String expectedContent = FileUtils.readFileToString(new File("./pom.xml"));
		Class<?> jsInterfaceClass = JSInterface.class;
		when(javaScript.source()).thenReturn("pom.xml");

		JavaScriptSource result = bean.load(javaScript, jsInterfaceClass, vaniContext);

		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong content: ", expectedContent, result.getSource().trim());
		Assert.assertEquals("wrong js-interface: ", jsInterfaceClass, result.getJsInterface());
		verify(javaScript, times(1)).sources();
		verify(javaScript, times(2)).source();
	}

}
