package org.vani.core.locating.page;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.vani.core.VaniContext;
import org.vani.core.annotation.UrlMapping;
import org.vani.core.util.VaniReflectionUtil;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPageHandlerFactoryTest {
	private DefaultPageHandlerFactory bean;

	@Mock
	protected VaniContext vaniContext;
	@Mock
	protected VaniReflectionUtil reflectionUtil;
	@Mock
	protected TestPageHandlerWithClassMethodMapping handler;
	@Mock
	protected ApplicationContext appContext;

	@Before
	public void setUp() {
		bean = new DefaultPageHandlerFactory();
		bean.vaniContext = vaniContext;
		bean.reflectionUtil = reflectionUtil;
		when(reflectionUtil.getAnnotatedMethodsWith(anyObject(), anyObject(), anyObject())).thenCallRealMethod();
		when(vaniContext.getAppContext()).thenReturn(appContext);
	}

	/**
	 * tests {@link DefaultPageHandlerFactory#getMethodMap} when provided
	 * handler class has no {@link UrlMapping} classes.
	 * <p>
	 * As result, empty map must be returned, because no url patterns are
	 * declared.
	 * </p>
	 */
	@Test
	public void testGetMethodMapWithoutMappings() {
		System.out.println("testGetMethodMapWithoutMappings");

		Class<?> handlerClass = TestPageHandlerWithoutMapping.class;
		Map<Pattern, Method> result = bean.getMethodMap(handlerClass);

		verify(reflectionUtil, times(1)).getAnnotatedMethodsWith(handlerClass, UrlMapping.class, null);
		verify(vaniContext, times(0)).resolveExpression(anyString());
		Assert.assertTrue("result must be empty, because there are no url patterns!", result.isEmpty());
	}

	/**
	 * tests {@link DefaultPageHandlerFactory#getMethodMap} when provided
	 * handler class only has {@link UrlMapping} on method level.
	 * <p>
	 * As result, map must be contains all annotated methods with correct
	 * pattern.
	 * </p>
	 * 
	 * @throws Exception
	 * 			@throws
	 */
	@Test
	public void testGetMethodMapWithMethodMappings() throws Exception {
		System.out.println("testGetMethodMapWithMethodMappings");

		Class<?> handlerClass = TestPageHandlerWithMethodMapping.class;
		Map<String, Method> expected = new HashMap<>();
		expected.put("/article/", handlerClass.getDeclaredMethod("article", String.class));
		expected.put("/category/", handlerClass.getDeclaredMethod("category", String.class));
		when(vaniContext.resolveExpression("/article/")).thenReturn("/article/");
		when(vaniContext.resolveExpression("/category/")).thenReturn("/category/");

		Map<Pattern, Method> result = bean.getMethodMap(handlerClass);

		verify(reflectionUtil, times(1)).getAnnotatedMethodsWith(handlerClass, UrlMapping.class, null);
		verify(vaniContext, times(2)).resolveExpression(anyString());
		assertPatternMap("wrong result: ", expected, result);
	}

	/**
	 * tests {@link DefaultPageHandlerFactory#getMethodMap} when provided
	 * handler class has {@link UrlMapping} on method and class level.
	 * <p>
	 * As result, map must be contains all annotated methods with pattern
	 * prefixed by classlevel value.
	 * </p>
	 * 
	 * @throws Exception
	 * 			@throws
	 */
	@Test
	public void testGetMethodMap() throws Exception {
		System.out.println("testGetMethodMap");

		Class<?> handlerClass = TestPageHandlerWithClassMethodMapping.class;
		Map<String, Method> expected = new HashMap<>();
		expected.put("master-blog.com/article/", handlerClass.getDeclaredMethod("article", String.class));
		expected.put("master-blog.com/category/", handlerClass.getDeclaredMethod("category", String.class));
		when(vaniContext.resolveExpression("${blog.url}/article/")).thenReturn("master-blog.com/article/");
		when(vaniContext.resolveExpression("${blog.url}/category/")).thenReturn("master-blog.com/category/");

		Map<Pattern, Method> result = bean.getMethodMap(handlerClass);

		verify(reflectionUtil, times(1)).getAnnotatedMethodsWith(handlerClass, UrlMapping.class, null);
		verify(vaniContext, times(2)).resolveExpression(anyString());
		assertPatternMap("wrong result: ", expected, result);
	}

	/**
	 * tests {@link DefaultPageHandlerFactory#create} when provided handler
	 * class has {@link UrlMapping} on method and class level.
	 * <p>
	 * As result, {@code PageHandler} must be returned which wraps the provided
	 * handler class.
	 * </p>
	 * 
	 * @throws Exception
	 * 			@throws
	 */
	@Test
	public void testCreate() throws Exception {
		System.out.println("testCreate");

		Class<TestPageHandlerWithClassMethodMapping> handlerClass = TestPageHandlerWithClassMethodMapping.class;
		List<String> expected = new ArrayList<>();
		expected.add("master-blog.com/article/");
		expected.add("master-blog.com/category/");
		when(vaniContext.resolveExpression("${blog.url}/article/")).thenReturn("master-blog.com/article/");
		when(vaniContext.resolveExpression("${blog.url}/category/")).thenReturn("master-blog.com/category/");
		when(vaniContext.createBean(handlerClass)).thenReturn(handler);

		PageHandler<?> result = bean.create(handlerClass);

		verify(vaniContext, times(1)).createBean(handlerClass);
		verify(reflectionUtil, times(1)).getAnnotatedMethodsWith(handlerClass, UrlMapping.class, null);
		verify(vaniContext, times(2)).resolveExpression(anyString());
		assertSet("wrong url patterns: ", expected, result.getUrlPatterns());
	}

	protected void assertPatternMap(String msg, Map<String, Method> expected, Map<Pattern, Method> actual) {
		Map<String, Method> actualPatternMap = new HashMap<>();
		actual.keySet().forEach(k -> actualPatternMap.put(k.pattern(), actual.get(k)));
		for (Object key : expected.keySet()) {
			Assert.assertEquals(msg + key + ": wrong value: ", expected.get(key), actualPatternMap.get(key));
		}
		Assert.assertEquals(msg + " unexpected count: ", expected.size(), actual.size());
	}

	protected void assertSet(String msg, List<?> expected, Set<?> actual) {
		for (Object exp : expected) {
			Assert.assertTrue(msg + exp + ": not found!", actual.contains(exp));
		}

		Assert.assertEquals(msg + "wrong count: ", expected.size(), actual.size());
	}

	class TestPageHandlerWithoutMapping {

		public void article(String url) {
		}
	}

	class TestPageHandlerWithMethodMapping {
		@UrlMapping("/article/")
		public void article(String url) {
		}

		@UrlMapping("/category/")
		public void category(String url) {
		}
	}

	@UrlMapping("${blog.url}")
	class TestPageHandlerWithClassMethodMapping {
		@UrlMapping("/article/")
		public void article(String url) {
		}

		@UrlMapping("/category/")
		public void category(String url) {
		}
	}
}
