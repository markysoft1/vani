package org.markysoft.vani.core.locating;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.AjaxWait;
import org.markysoft.vani.core.annotation.ContentWait;
import org.markysoft.vani.core.locating.RegionObject;
import org.markysoft.vani.core.locating.WebElementLocator;
import org.markysoft.vani.core.util.FieldTypeInfo;
import org.markysoft.vani.core.wait.WaitBuilder;
import org.markysoft.vani.core.wait.WaitUtil;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class WebElementLocatorTest {
	private WebElementLocator bean;

	@Mock
	private VaniContext vaniContext;
	@Mock
	private FieldTypeInfo fieldTypeInfo;
	@Mock
	private WebDriver webDriver;
	@Mock
	private SearchContext searchContext;
	@Mock
	private By by;
	@Mock
	private ApplicationContext appContext;
	@Mock
	private WebElement element;
	@Mock
	private WebElement element2;
	@Mock
	private RemoteWebElement remoteElement;
	@Mock
	private RegionObject regionObject;
	@Mock
	private WaitUtil waitUtil;
	@Mock
	private AjaxWait ajaxWait;
	@Mock
	private ContentWait contentWait;
	@Mock
	private WaitBuilder waitBuilder;

	@Before
	public void setUp() {
		when(vaniContext.getAppContext()).thenReturn(appContext);
		when(fieldTypeInfo.getWebDriver()).thenReturn(webDriver);
		when(remoteElement.getWrappedDriver()).thenReturn(webDriver);
		when(appContext.getBean(WaitUtil.class)).thenReturn(waitUtil);
	}

	/**
	 * tests {@link WebElementLocator#findElements()} when no cached elements
	 * are available.
	 * <p>
	 * As result, {@code by} must be executed and result should be cached.
	 * </p>
	 */
	@Test
	public void testFindElementsWithEmptyCache() {
		System.out.println("testFindElementsWithEmptyCache");

		initBean(false);
		when(searchContext.findElements(by)).thenReturn(Arrays.asList(element, element2));

		List<WebElement> result = bean.findElements();

		verify(searchContext, times(1)).findElements(by);
		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong count: ", 2, result.size());
		Assert.assertEquals("wrong obj at idx 0: ", element, result.get(0));
		Assert.assertEquals("wrong obj at idx 1: ", element2, result.get(1));
		Assert.assertEquals("wrong cached elements: ", result, bean.cachedElementList);
	}

	/**
	 * tests {@link WebElementLocator#findElements()} when cached elements are
	 * available.
	 * <p>
	 * As result, {@code by} must not be executed, because there are cached
	 * elements.
	 * </p>
	 */
	@Test
	public void testFindElementsWithCachedElements() {
		System.out.println("testFindElementsWithCachedElements");

		initBean(false);
		List<WebElement> cachedElements = Arrays.asList(element, element2);
		bean.cachedElementList = cachedElements;
		when(searchContext.findElements(by)).thenReturn(Arrays.asList(element, element2));

		List<WebElement> result = bean.findElements();

		verify(searchContext, times(0)).findElements(by);
		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong count: ", 2, result.size());
		Assert.assertEquals("wrong obj at idx 0: ", element, result.get(0));
		Assert.assertEquals("wrong obj at idx 1: ", element2, result.get(1));
		Assert.assertEquals("wrong cached elements (returned != cached): ", result, bean.cachedElementList);
		Assert.assertEquals("wrong cached elements: ", cachedElements, bean.cachedElementList);
	}

	/**
	 * tests {@link WebElementLocator#findElements()} when cached elements are
	 * available, but they are invalidated.
	 * <p>
	 * As result, {@code by} must be executed and result should be cached,
	 * because cache was invalidated.
	 * </p>
	 */
	@Test
	public void testFindElementsWithInvalidatedCachedElements() {
		System.out.println("testFindElementsWithInvalidatedCachedElements");

		initBean(false);
		List<WebElement> cachedElements = Arrays.asList(element);
		bean.cachedElementList = cachedElements;
		when(fieldTypeInfo.getBean()).thenReturn(regionObject);
		when(regionObject.isInvalidated()).thenReturn(true);
		when(searchContext.findElements(by)).thenReturn(Arrays.asList(element, element2));
		List<WebElement> result = bean.findElements();

		verify(searchContext, times(1)).findElements(by);
		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong count: ", 2, result.size());
		Assert.assertEquals("wrong obj at idx 0: ", element, result.get(0));
		Assert.assertEquals("wrong obj at idx 1: ", element2, result.get(1));
		Assert.assertEquals("wrong cached elements (returned != cached): ", result, bean.cachedElementList);
		Assert.assertNotEquals("wrong cached elements: ", cachedElements, bean.cachedElementList);
	}

	/**
	 * tests {@link WebElementLocator#findElement()} when no cached element is
	 * available.
	 * <p>
	 * As result, {@code by} must be executed and result should be cached.
	 * </p>
	 */
	@Test
	public void testFindElementWithEmptyCache() {
		System.out.println("testFindElementWithEmptyCache");

		initBean(true);
		when(searchContext.findElement(by)).thenReturn(element);

		WebElement result = bean.findElement();

		verify(searchContext, times(1)).findElement(by);
		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong result: ", element, result);
		Assert.assertEquals("wrong cached element: ", result, bean.cachedElement);
	}

	/**
	 * tests {@link WebElementLocator#findElement()} when no cached element is
	 * available and caching is disabled.
	 * <p>
	 * As result, {@code by} must be executed and result should not be cached.
	 * </p>
	 */
	@Test
	public void testFindElementWithEmptyCacheAndDisabledCache() {
		System.out.println("testFindElementWithEmptyCacheAndDisabledCache");

		initBean(false);
		when(searchContext.findElement(by)).thenReturn(element);

		WebElement result = bean.findElement();

		verify(searchContext, times(1)).findElement(by);
		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong result: ", element, result);
		Assert.assertEquals("wrong cached element: ", null, bean.cachedElement);
	}

	/**
	 * tests {@link WebElementLocator#findElement()} when cached element is
	 * available.
	 * <p>
	 * As result, {@code by} must not be executed, because there is a cached
	 * element.
	 * </p>
	 */
	@Test
	public void testFindElementWithCachedElement() {
		System.out.println("testFindElementWithCachedElement");

		initBean(true);
		WebElement cachedElement = element;
		bean.cachedElement = cachedElement;
		when(searchContext.findElement(by)).thenReturn(element);

		WebElement result = bean.findElement();

		verify(searchContext, times(0)).findElement(by);
		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong result: ", element, result);
		Assert.assertEquals("wrong cached element (returned != cached): ", result, bean.cachedElement);
		Assert.assertEquals("wrong cached element: ", cachedElement, bean.cachedElement);
	}

	/**
	 * tests {@link WebElementLocator#findElement()} when cached element is
	 * available, but it is invalidated.
	 * <p>
	 * As result, {@code by} must be executed and result should be cached,
	 * because cache was invalidated.
	 * </p>
	 */
	@Test
	public void testFindElementWithInvalidatedCachedElements() {
		System.out.println("testFindElementWithInvalidatedCachedElements");

		initBean(true);
		WebElement cachedElement = element2;
		bean.cachedElement = cachedElement;
		when(fieldTypeInfo.getBean()).thenReturn(regionObject);
		when(regionObject.isInvalidated()).thenReturn(true);
		when(searchContext.findElement(by)).thenReturn(element);

		WebElement result = bean.findElement();

		verify(searchContext, times(1)).findElement(by);
		Assert.assertNotNull("result must not be NULL!", result);
		Assert.assertEquals("wrong result: ", element, result);
		Assert.assertEquals("wrong cached element (returned != cached): ", result, bean.cachedElement);
		Assert.assertNotEquals("wrong cached element: ", cachedElement, bean.cachedElement);
	}

	/**
	 * tests {@link WebElementLocator#getWebDriver()} when {@code searchContext}
	 * is not supported.
	 * <p>
	 * As result, {@code NULL} must be returned, because {@code searchContext}
	 * is not supported.
	 * </p>
	 */
	@Test
	public void testGetWebDriverFromNotSupportedType() {
		System.out.println("testGetWebDriverFromNotSupportedType");

		initBean(false);

		WebDriver result = bean.getWebDriver();

		Assert.assertNull("result must be NULL, because search context is not supported!", result);
	}

	/**
	 * tests {@link WebElementLocator#getWebDriver()} when {@code searchContext}
	 * is {@link WebDriver}.
	 * <p>
	 * As result, provided {@code webDriver} must be returned.
	 * </p>
	 */
	@Test
	public void testGetWebDriverFromWebDriverAsSearchContext() {
		System.out.println("testGetWebDriverFromWebDriverAsSearchContext");

		initBean(webDriver, false);

		WebDriver result = bean.getWebDriver();

		Assert.assertEquals("wrong result: ", webDriver, result);
	}

	/**
	 * tests {@link WebElementLocator#getWebDriver()} when {@code searchContext}
	 * is {@link RemoteWebElement}.
	 * <p>
	 * As result, wrapped {@code webDriver} of provided {@link RemoteWebElement}
	 * must be returned.
	 * </p>
	 */
	@Test
	public void testGetWebDriverFromWebElementAsSearchContext() {
		System.out.println("testGetWebDriverFromWebElementAsSearchContext");

		initBean(remoteElement, false);

		WebDriver result = bean.getWebDriver();

		verify(remoteElement, times(1)).getWrappedDriver();
		Assert.assertEquals("wrong result: ", webDriver, result);
	}

	/**
	 * tests {@link WebElementLocator#executeContentWait()} when no
	 * content-waits are configured
	 * <p>
	 * As result, nothing should be done.
	 * </p>
	 */
	@Test
	public void testExecuteContentWaitWithoutWaits() {
		System.out.println("testExecuteContentWaitWithoutWaits");

		initBean(false);

		bean.executeContentWait();

		verify(vaniContext, times(0)).resolveExpression(anyString());
		verify(waitUtil, times(0)).ajaxJQuery(anyLong(), anyObject());
	}

	/**
	 * tests {@link WebElementLocator#executeContentWait()} when
	 * {@link AjaxWait} is configured
	 * <p>
	 * As result, {@link AjaxWait} must be executed.
	 * </p>
	 */
	@Test
	public void testExecuteContentWaitWithAjaxWait() {
		System.out.println("testExecuteContentWaitWithAjaxWait");

		initBean(false);
		when(fieldTypeInfo.getAjaxWait()).thenReturn(ajaxWait);
		when(ajaxWait.value()).thenReturn(5000);

		bean.executeContentWait();

		verify(vaniContext, times(0)).resolveExpression(anyString());
		verify(waitUtil, times(1)).ajaxJQuery(eq(5000L), anyObject());
	}

	/**
	 * tests {@link WebElementLocator#executeContentWait()} when
	 * {@link ContentWait} is configured
	 * <p>
	 * As result, {@link ContentWait} must be executed.
	 * </p>
	 */
	@Test
	public void testExecuteContentWaitWithContentWait() {
		System.out.println("testExecuteContentWaitWithContentWait");

		initBean(webDriver, false);
		String selector = "${content.jq.progressBar}";
		String condition = "${content.jq.condition.progressBar}";
		String expectedSelector = "#loading";
		String expectedCondition = "hasMatches()";
		when(fieldTypeInfo.getContentWait()).thenReturn(contentWait);
		when(contentWait.timeout()).thenReturn(5000);
		when(contentWait.pollingTime()).thenReturn(1000);
		when(contentWait.value()).thenReturn(selector);
		when(contentWait.condition()).thenReturn(condition);
		when(vaniContext.resolveExpression(selector)).thenReturn(expectedSelector);
		when(vaniContext.resolveExpression(condition)).thenReturn(expectedCondition);
		when(waitUtil.element(expectedSelector, webDriver)).thenReturn(waitBuilder);
		when(waitBuilder.spel(expectedCondition)).thenReturn(waitBuilder);
		when(waitBuilder.until(5000L, 1000L, webDriver)).thenReturn(true);

		bean.executeContentWait();

		verify(vaniContext, times(1)).resolveExpression(selector);
		verify(waitBuilder, times(1)).until(5000L, 1000L, webDriver);
		verify(waitUtil, times(0)).ajaxJQuery(anyLong(), anyObject());
	}

	protected void initBean(boolean shouldBeCached) {
		initBean(searchContext, shouldBeCached);
	}

	protected void initBean(SearchContext searchContext, boolean shouldBeCached) {
		bean = new WebElementLocator(searchContext, by, shouldBeCached, fieldTypeInfo, vaniContext);

	}

}
