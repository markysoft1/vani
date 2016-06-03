package org.markysoft.vani.core.locating;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.PageUrl;
import org.markysoft.vani.core.locating.PageNavigationException;
import org.markysoft.vani.core.locating.PageObject;
import org.markysoft.vani.core.util.VaniReflectionUtil;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;

@RunWith(MockitoJUnitRunner.class)
public class PageObjectTest {
	private PageObject bean;

	@Mock
	private VaniReflectionUtil reflectionUtil;
	@Mock
	private VaniContext vaniContext;
	@Mock
	private WebDriver webDriver;

	@Mock
	private PageUrl pageUrl;

	@Before
	public void setUp() throws Exception {
		initBean(new PageObjectWithPageUrlAnnotation());
	}

	protected void initBean(PageObject pageObject) throws Exception {
		bean = pageObject;

		bean.reflectionUtil = reflectionUtil;
		bean.webDriver = webDriver;
		bean.vaniContext = vaniContext;
	}

	/**
	 * tests {@link PageObject#to()} when current {@link PageObject} has no
	 * {@code pageUrl}, but implementation class is annotated with
	 * {@link PageUrl}.
	 * <p>
	 * As result, specified url of {@link PageUrl} annotation must be used as
	 * {@code pageUrl} by {@code webDriver}
	 * </p>
	 */
	@Test
	public void testToWithPageUrlAnnotation() {
		System.out.println("testMustInjectWithoutDectectionScript");

		String expected = "www.like-me.com";
		when(reflectionUtil.getTypeAnnotation(PageUrl.class, PageObjectWithPageUrlAnnotation.class))
				.thenReturn(pageUrl);
		when(pageUrl.value()).thenReturn(expected);
		when(vaniContext.resolveExpression(expected)).thenReturn(expected);

		bean.to();

		verify(reflectionUtil, times(1)).getTypeAnnotation(PageUrl.class, PageObjectWithPageUrlAnnotation.class);
		verify(reflectionUtil, times(0)).getAnnotatedMethodWith(anyObject(), eq(PageUrl.class), eq(String.class));
		verify(vaniContext, times(1)).resolveExpression(expected);
		verify(webDriver, times(1)).get(expected);
		Assert.assertEquals("wrong pageUrl: ", expected, bean.pageUrl);
	}

	/**
	 * tests {@link PageObject#to()} when current {@link PageObject} has no
	 * {@code pageUrl}, but implementation class has a method annotated with
	 * {@link PageUrl}.
	 * <p>
	 * As result, annotated method must be called to get url, which is used by
	 * {@code webDriver}
	 * </p>
	 */
	@Test
	public void testToWithoutPageUrlAnnotationButWithGetter() throws Exception {
		System.out.println("testToWithoutPageUrlAnnotationButWithGetter");

		initBean(new PageObjectWithPageUrlGetter());
		Method method = PageObjectWithPageUrlGetter.class.getDeclaredMethod("getPageUrl");
		String expected = "www.like-me.com";
		when(reflectionUtil.getAnnotatedMethodWith(PageObjectWithPageUrlGetter.class, PageUrl.class, String.class))
				.thenReturn(method);
		when(vaniContext.resolveExpression(expected)).thenReturn(expected);

		bean.to();

		verify(reflectionUtil, times(1)).getTypeAnnotation(PageUrl.class, PageObjectWithPageUrlGetter.class);
		verify(vaniContext, times(1)).resolveExpression(expected);
		verify(webDriver, times(1)).get(expected);
		Assert.assertEquals("wrong pageUrl: ", expected, bean.pageUrl);
	}

	/**
	 * tests {@link PageObject#to()} when current {@link PageObject} has
	 * {@code pageUrl} with placeholder.
	 * <p>
	 * As result, resolved pageUrl must be used by webDriver
	 * </p>
	 */
	@Test
	public void testToWithPageUrlAsExpr() throws Exception {
		System.out.println("testToWithPageUrlAsExpr");

		initBean(new PageObject() {
		});
		String pageUrl = "${coolPage.url}";
		bean.pageUrl = pageUrl;
		String expected = "www.like-me.com";
		when(vaniContext.resolveExpression(pageUrl)).thenReturn(expected);

		bean.to();

		verify(reflectionUtil, times(0)).getTypeAnnotation(PageUrl.class, PageObjectWithPageUrlGetter.class);
		verify(vaniContext, times(1)).resolveExpression(pageUrl);
		verify(webDriver, times(1)).get(expected);
		Assert.assertEquals("wrong pageUrl: ", expected, bean.pageUrl);
	}

	/**
	 * tests {@link PageObject#to()} when current {@link PageObject} has no
	 * {@code pageUrl}, no class level {@link PageUrl} annotation or method
	 * annotated with {@link PageUrl}.
	 * <p>
	 * As result, {@link PageNavigationException} must be thrown, because no
	 * {@code pageUrl} is available to call
	 * </p>
	 */
	@Test(expected = PageNavigationException.class)
	public void testToWithoutPageUrlAnnotationAndGetter() throws Exception {
		System.out.println("testToWithoutPageUrlAnnotationAndGetter");

		bean.to();
	}

	@PageUrl("www.like-me.com")
	class PageObjectWithPageUrlAnnotation extends PageObject {
	}

	class PageObjectWithPageUrlGetter extends PageObject {
		@PageUrl
		public String getPageUrl() {
			return "www.like-me.com";
		}
	}
}
