package org.markysoft.vani.core.locating.page;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.locating.FragmentObject;
import org.markysoft.vani.core.locating.PageObject;
import org.markysoft.vani.core.locating.factory.RegionFactory;
import org.markysoft.vani.core.locating.page.DefaultPageHandler;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPageHandlerTest {
	private DefaultPageHandler bean;

	@Mock
	protected VaniContext vaniContext;
	@Mock
	protected RegionFactory regionFactory;
	@Mock
	protected TestHandler testHandler;

	@Mock
	protected ApplicationContext appContext;
	@Mock
	protected Map<Pattern, Method> methodMap;
	@Mock
	protected WebDriver webDriver;
	@Mock
	protected TestPage page;
	@Mock
	protected TestFragment fragment;

	@Before
	public void setUp() {
		when(vaniContext.getAppContext()).thenReturn(appContext);
		when(appContext.getBean(RegionFactory.class)).thenReturn(regionFactory);

		bean = new DefaultPageHandler(testHandler, vaniContext);
		bean.methodMap = methodMap;

		when(regionFactory.createPage(eq(TestPage.class), anyObject(), anyString())).thenReturn(page);
		when(regionFactory.create(eq(TestFragment.class), (WebDriver) anyObject())).thenReturn(fragment);
	}

	/**
	 * tests {@link DefaultPageHandler#getUrlPatterns()} when bean has no url
	 * mappings.
	 * <p>
	 * As result, empty list must be returned, because bean has no url mappings.
	 * </p>
	 */
	@Test
	public void testGetUrlPatternsWithoutMethods() {
		System.out.println("testGetUrlPatternsWithoutMethods");

		when(methodMap.keySet()).thenReturn(new HashSet<Pattern>());

		Set<String> result = bean.getUrlPatterns();

		Assert.assertTrue("result must be empty, because method map is empty!", result.isEmpty());
	}

	/**
	 * tests {@link DefaultPageHandler#getUrlPatterns()} when bean has url
	 * mappings.
	 * <p>
	 * As result, returned list must contains all patterns of url mappings.
	 * </p>
	 */
	@Test
	public void testGetUrlPatternsWithMethods() {
		System.out.println("testGetUrlPatternsWithMethods");

		List<String> expectedUrls = Arrays.asList("/c/", "/c/*./document/");
		Set<Pattern> patterns = new HashSet<>(expectedUrls.size());
		expectedUrls.forEach(u -> patterns.add(Pattern.compile(u)));
		when(methodMap.keySet()).thenReturn(patterns);

		Set<String> result = bean.getUrlPatterns();

		assertSet("wrong result: ", expectedUrls, result);
	}

	/**
	 * tests {@link DefaultPageHandler#setMethodMap(Map)} when provided map is
	 * empty.
	 * <p>
	 * As result, method map of instance must be replaced.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetMethodMapWithEmptyMap() {
		System.out.println("testSetMethodMapWithEmptyMap");

		Map<Pattern, Method> methodMap = new HashMap<>();
		bean.setMethodMap(methodMap);

		verify(this.methodMap, times(0)).clear();
		Assert.assertEquals("wrong methodMap: ", methodMap, bean.methodMap);
	}

	/**
	 * tests {@link DefaultPageHandler#setMethodMap(Map)} when provided map is
	 * not empty.
	 * <p>
	 * As result, provided method map must be used as method map of bean.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetMethodMap() {
		System.out.println("testSetMethodMap");

		Map<Pattern, Method> methodMap = new HashMap<>();
		methodMap.put(Pattern.compile("/c/"), null);
		bean.setMethodMap(methodMap);

		verify(this.methodMap, times(0)).clear();
		Assert.assertEquals("wrong method map: ", methodMap, bean.methodMap);
	}

	/**
	 * tests {@link DefaultPageHandler#setMethodMap(Map)} when {@code NULL} as
	 * argument is provided.
	 * <p>
	 * As result, method map of bean must be cleared.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetMethodMapWithNull() {
		System.out.println("testSetMethodMapWithNull");

		bean.setMethodMap(null);

		verify(this.methodMap, times(1)).clear();
	}

	/**
	 * tests {@link DefaultPageHandler#getApplicable(String)} when
	 * {@code methodMap} is empty.
	 * <p>
	 * As result, {@code NULL} must be returned, because {@code methodMap} of
	 * testing instance is empty.
	 * </p>
	 */
	@Test
	public void testGetApplicableWithEmptyMap() {
		System.out.println("testGetApplicableWithEmptyMap");

		when(methodMap.keySet()).thenReturn(new HashSet<Pattern>());

		Method result = bean.getApplicable("http://www.something.com/c/500/document/intro.pdf");

		verify(this.methodMap, times(1)).keySet();
		Assert.assertNull("result must be NULL, because methodMap was empty!", result);
	}

	/**
	 * tests {@link DefaultPageHandler#getApplicable(String)} when
	 * {@code methodMap} has similar patterns.
	 * <p>
	 * As result, method with best match must be returned.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetApplicableWithPatterns() throws Exception {
		System.out.println("testGetApplicableWithPatterns");

		Method method = getClass().getMethod("setUp");
		Method method2 = getClass().getMethod("testGetApplicableWithEmptyMap");
		Method method3 = getClass().getMethod("testGetApplicableWithPatterns");
		Method method4 = getClass().getMethod("testSetMethodMapWithNull");
		Map<Pattern, Method> methodMap = new LinkedHashMap<>();
		methodMap.put(Pattern.compile(".com/all/"), method);
		methodMap.put(Pattern.compile("/c/"), method2);
		methodMap.put(Pattern.compile("/c/.*/document/"), method3);
		methodMap.put(Pattern.compile("/c/50"), method4);
		bean.setMethodMap(methodMap);

		Method result = bean.getApplicable("http://www.something.com/c/500/document/intro.pdf");

		Assert.assertEquals("wrong result: ", method3, result);
	}

	/**
	 * tests {@link DefaultPageHandler#isApplicable(String)} when
	 * {@code methodMap} contains matching.
	 * <p>
	 * As result, true must be returned, because matching is available.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testIsApplicableWithPatterns() throws Exception {
		System.out.println("testGetApplicableWithPatterns");

		Method method2 = getClass().getMethod("testGetApplicableWithEmptyMap");
		Map<Pattern, Method> methodMap = new LinkedHashMap<>();
		methodMap.put(Pattern.compile("/c/"), method2);
		bean.setMethodMap(methodMap);

		boolean result = bean.isApplicable("http://www.something.com/c/500/document/intro.pdf");

		Assert.assertTrue("wrong result: ", result);
	}

	/**
	 * tests {@link DefaultPageHandler#isApplicable(String)} when
	 * {@code methodMap} contains no matching.
	 * <p>
	 * As result, false must be returned, because matching is not available.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testIsApplicableFalse() throws Exception {
		System.out.println("testIsApplicableFalse");

		Method method2 = getClass().getMethod("testGetApplicableWithEmptyMap");
		Map<Pattern, Method> methodMap = new LinkedHashMap<>();
		methodMap.put(Pattern.compile("/cd/"), method2);
		bean.setMethodMap(methodMap);

		boolean result = bean.isApplicable("http://www.something.com/c/500/document/intro.pdf");

		Assert.assertFalse("wrong result: ", result);
	}

	/**
	 * tests {@link DefaultPageHandler#handle(String, WebDriver)} when
	 * {@code methodMap} contains no matching.
	 * <p>
	 * As result, nothing should be done, because no applicable method is
	 * available.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleWithoutMatch() throws Exception {
		System.out.println("testHandleWithoutMatch");

		Method method2 = getClass().getMethod("testGetApplicableWithEmptyMap");
		Map<Pattern, Method> methodMap = new LinkedHashMap<>();
		methodMap.put(Pattern.compile("/cd/"), method2);
		bean.setMethodMap(methodMap);

		bean.handle("http://www.something.com/c/500/document/intro.pdf", webDriver);
	}

	/**
	 * tests {@link DefaultPageHandler#handle(String, WebDriver)} when
	 * {@code methodMap} contains matching and corresponding mehtod has no
	 * arguments.
	 * <p>
	 * As result, corresponding method should be invoked.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleWithoutArguments() throws Exception {
		System.out.println("testHandleWithoutArguments");

		Method method = TestHandler.class.getMethod("handleWithoutArgs");
		Map<Pattern, Method> methodMap = new LinkedHashMap<>();
		methodMap.put(Pattern.compile("/c/"), method);
		bean.setMethodMap(methodMap);

		bean.handle("http://www.something.com/c/500/document/intro.pdf", webDriver);

		verify(testHandler, times(1)).handleWithoutArgs();
	}

	/**
	 * tests {@link DefaultPageHandler#handle(String, WebDriver)} when
	 * {@code methodMap} contains matching and corresponding method has two
	 * string parameters.
	 * <p>
	 * As result, method with url as first argument should be invoked.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleWithUrlArgument() throws Exception {
		System.out.println("testHandleWithUrlArgument");

		Method method = TestHandler.class.getMethod("handleWithUrl", String.class, String.class);
		Map<Pattern, Method> methodMap = new LinkedHashMap<>();
		methodMap.put(Pattern.compile("/c/"), method);
		bean.setMethodMap(methodMap);

		String url = "http://www.something.com/c/500/document/intro.pdf";
		bean.handle(url, webDriver);

		verify(testHandler, times(1)).handleWithUrl(url, null);
	}

	/**
	 * tests {@link DefaultPageHandler#handle(String, WebDriver)} when
	 * {@code methodMap} contains matching and corresponding method has
	 * webDriver and string as parameters.
	 * <p>
	 * As result, method with webDriver and url should be invoked.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleWithUrlAndWebDriverArgument() throws Exception {
		System.out.println("testHandleWithUrlAndWebDriverArgument");

		Method method = TestHandler.class.getMethod("handleWithUrlAndWebDriver", WebDriver.class, String.class);
		Map<Pattern, Method> methodMap = new LinkedHashMap<>();
		methodMap.put(Pattern.compile("/c/"), method);
		bean.setMethodMap(methodMap);

		String url = "http://www.something.com/c/500/document/intro.pdf";
		bean.handle(url, webDriver);

		verify(testHandler, times(1)).handleWithUrlAndWebDriver(webDriver, url);
	}

	/**
	 * tests {@link DefaultPageHandler#handle(String, WebDriver)} when
	 * {@code methodMap} contains matching and corresponding method has
	 * pageObject, webDriver and string as parameters.
	 * <p>
	 * As result, method with pageObject, webDriver and url should be invoked.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleWithUrlPageAndWebDriverArgument() throws Exception {
		System.out.println("testHandleWithUrlPageAndWebDriverArgument");

		Method method = TestHandler.class.getMethod("handle", TestPage.class, WebDriver.class, String.class);
		Map<Pattern, Method> methodMap = new LinkedHashMap<>();
		methodMap.put(Pattern.compile("/c/"), method);
		bean.setMethodMap(methodMap);

		String url = "http://www.something.com/c/500/document/intro.pdf";
		bean.handle(url, webDriver);

		verify(testHandler, times(1)).handle(page, webDriver, url);
	}

	/**
	 * tests {@link DefaultPageHandler#handle(String, WebDriver)} when
	 * {@code methodMap} contains matching and corresponding method has
	 * regionObject, webDriver and string as parameters.
	 * <p>
	 * As result, method with regionObject, webDriver and url should be invoked.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleWithUrlRegionAndWebDriverArgument() throws Exception {
		System.out.println("testHandleWithUrlRegionAndWebDriverArgument");

		Method method = TestHandler.class.getMethod("handle", TestFragment.class, WebDriver.class, String.class);
		Map<Pattern, Method> methodMap = new LinkedHashMap<>();
		methodMap.put(Pattern.compile("/c/"), method);
		bean.setMethodMap(methodMap);

		String url = "http://www.something.com/c/500/document/intro.pdf";
		bean.handle(url, webDriver);

		verify(testHandler, times(1)).handle(fragment, webDriver, url);
	}

	protected void assertSet(String msg, List<?> expected, Set<?> actual) {
		for (Object exp : expected) {
			Assert.assertTrue(msg + exp + ": not found!", actual.contains(exp));
		}

		Assert.assertEquals(msg + "wrong count: ", expected.size(), actual.size());
	}

	class TestHandler {
		public void handleWithoutArgs() {
		}

		public void handleWithUrl(String url, String value) {
		}

		public void handleWithUrlAndWebDriver(WebDriver webDriver, String url) {
		}

		public void handle(TestPage page, WebDriver webDriver, String url) {
		}

		public void handle(TestFragment frag, WebDriver webDriver, String url) {
		}
	}

	class TestPage extends PageObject {
	}

	class TestFragment extends FragmentObject {
	}
}
