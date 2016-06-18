package org.markysoft.vani.core.locating.page;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.javascript.JQuery;
import org.markysoft.vani.core.javascript.LinkUtils;
import org.markysoft.vani.core.locating.JQueryElement;
import org.markysoft.vani.core.locating.PageMarkerHandler;
import org.markysoft.vani.core.wait.WaitUtil;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPageCrawlerTest {
	private DefaultPageCrawler bean;

	@Mock
	protected VaniContext vaniContext;
	@Mock
	protected PageHandlerFactory pageHandlerFactory;
	@Mock
	protected WaitUtil waitUtil;
	@Mock
	protected LinkUtils linkUtils;
	@Mock
	protected PageMarkerHandler pageMarkerHandler;

	@Mock
	private WebDriver webDriver;
	@Mock
	private Reflections reflections;
	@Mock
	private JQueryElement $element;
	@Mock
	private JQueryElement $element2;
	@Mock
	private ApplicationContext appContext;
	@Mock
	private JQuery jquery;
	@Mock
	private PageHandler pageHandler;
	@Mock
	private PageHandler pageHandler2;
	@Mock
	private WebDriver webDriver2;

	@Before
	public void setUp() {
		bean = new DefaultPageCrawler();
		init(bean);

		when(vaniContext.getReflections()).thenReturn(reflections);
		when(vaniContext.getAppContext()).thenReturn(appContext);
		when(appContext.getBean(JQuery.class)).thenReturn(jquery);
	}

	/**
	 * tests {@link DefaultPageCrawler#getApplicableUrls()} when bean has no url
	 * patterns.
	 * <p>
	 * As result, empty list must be returned, because bean has no url patterns.
	 * </p>
	 */
	@Test
	public void testGetApplicableUrlsWithoutUrlPatterns() {
		System.out.println("testGetApplicableUrlsWithoutUrlPatterns");

		bean.urlPatterns = new HashSet<>();

		List<String> result = bean.getApplicableUrls();

		Assert.assertTrue("result must be empty, because there are no url patterns!", result.isEmpty());
	}

	/**
	 * tests {@link DefaultPageCrawler#getApplicableUrls()} when bean has two
	 * url patterns.
	 * <p>
	 * As result, returned list must contains the href property of found jquery
	 * elements.
	 * </p>
	 */
	@Test
	public void testGetApplicableUrlsWithUrlPatterns() {
		System.out.println("testGetApplicableUrlsWithoutUrlPatterns");

		String[] patterns = { "/c/", "/c/.*/document/" };
		bean.urlPatterns = new TreeSet<>(Arrays.asList(patterns));
		String expectedHref = "www.something.com/c/524.html";
		String expectedHref2 = "www.something.com/c/524/document/4445.pdf";
		when(linkUtils.getApplicableUrls(anyObject())).thenReturn(Arrays.asList(expectedHref, expectedHref2));

		List<String> result = bean.getApplicableUrls();

		verify(linkUtils, times(1)).getApplicableUrls(patterns);
		Assert.assertEquals("wrong result: ", Arrays.asList(expectedHref, expectedHref2), result);
	}

	/**
	 * tests {@link DefaultPageCrawler#removeJSessionId(String)} when provided
	 * url doesn't have a jSessionId.
	 * <p>
	 * As result, specified url must be returned..
	 * </p>
	 */
	@Test
	public void testRemoveJSessionIdWithoutJSessionId() {
		System.out.println("testRemoveJSessionIdWithoutJSessionId");

		String url = "http://www.something.com/c/500.html";

		String result = bean.removeJSessionId(url);

		Assert.assertEquals("wrong result: ", url, result);
	}

	/**
	 * tests {@link DefaultPageCrawler#removeJSessionId(String)} when provided
	 * url contains a jSessionId.
	 * <p>
	 * As result, url without jSessionId must be returned.
	 * </p>
	 */
	@Test
	public void testRemoveJSessionIdWithJSessionId() {
		System.out.println("testRemoveJSessionIdWithJSessionId");

		String url = "http://www.something.com/c/500.html;jsessionid=1A530637289A03B07199A44E8D531427";
		String expected = "http://www.something.com/c/500.html";
		String result = bean.removeJSessionId(url);

		Assert.assertEquals("wrong result: ", expected, result);
	}

	/**
	 * tests {@link DefaultPageCrawler#removeJSessionId(String)} when provided
	 * url contains a uppercase jSessionId marker.
	 * <p>
	 * As result, url without jSessionId must be returned.
	 * </p>
	 */
	@Test
	public void testRemoveJSessionIdWithUpperCaseJSessionId() {
		System.out.println("testRemoveJSessionIdWithUpperCaseJSessionId");

		String url = "http://www.something.com/c/500.html;JSESSIONID=1A530637289A03B07199A44E8D531427";
		String expected = "http://www.something.com/c/500.html";
		String result = bean.removeJSessionId(url);

		Assert.assertEquals("wrong result: ", expected, result);
	}

	/**
	 * tests {@link DefaultPageCrawler#removeJSessionId(String)} when provided
	 * url contains a jSessionId and query string.
	 * <p>
	 * As result, url without jSessionId, but with query string, must be
	 * returned.
	 * </p>
	 */
	@Test
	public void testRemoveJSessionIdWithQueryString() {
		System.out.println("testRemoveJSessionIdWithQueryString");

		String url = "http://www.something.com/c/500.html?sortingOrder=ASC;JSESSIONID=1A530637289A03B07199A44E8D531427";
		String expected = "http://www.something.com/c/500.html?sortingOrder=ASC";
		String result = bean.removeJSessionId(url);

		Assert.assertEquals("wrong result: ", expected, result);
	}

	/**
	 * tests {@link DefaultPageCrawler#removeJSessionId(String)} when provided
	 * url contains a jSessionId followed by query string.
	 * <p>
	 * As result, url without jSessionId, but with query string, must be
	 * returned.
	 * </p>
	 */
	@Test
	public void testRemoveJSessionIdWithQueryStringAfterJSessionId() {
		System.out.println("testRemoveJSessionIdWithQueryStringAfterJSessionId");

		String url = "http://www.something.com/c/500.html;JSESSIONID=1A530637289A03B07199A44E8D531427?sortingOrder=ASC";
		String expected = "http://www.something.com/c/500.html?sortingOrder=ASC";
		String result = bean.removeJSessionId(url);

		Assert.assertEquals("wrong result: ", expected, result);
	}

	/**
	 * tests {@link DefaultPageCrawler#isVisited(String, Set)} when provided url
	 * contains a jSessionId and given {@code visitedUrls} is empty.
	 * <p>
	 * As result, false must be returned, because provided url is not already
	 * visited.
	 * </p>
	 */
	@Test
	public void testIsVisitedFalse() {
		System.out.println("testIsVisitedFalse");

		String url = "http://www.something.com/c/500.html?sortingOrder=ASC;JSESSIONID=1A530637289A03B07199A44E8D531427";
		Set<String> visitedUrls = new HashSet<>();

		boolean result = bean.isVisited(url, visitedUrls);

		Assert.assertEquals("wrong result: ", false, result);
	}

	/**
	 * tests {@link DefaultPageCrawler#isVisited(String, Set)} when provided url
	 * contains a jSessionId and given {@code visitedUrls} contains it.
	 * <p>
	 * As result, true must be returned, because provided url is already
	 * visited.
	 * </p>
	 */
	@Test
	public void testIsVisitedTrueWithJSessionId() {
		System.out.println("testIsVisitedFalse");

		String url = "http://www.something.com/c/500.html?sortingOrder=ASC;JSESSIONID=1A530637289A03B07199A44E8D531427";
		Set<String> visitedUrls = new HashSet<>(Arrays.asList("http://www.something.com/c/500.html?sortingOrder=ASC"));

		boolean result = bean.isVisited(url, visitedUrls);

		Assert.assertEquals("wrong result: ", true, result);
	}

	/**
	 * tests {@link DefaultPageCrawler#handle(String)} when no page handlers are
	 * available.
	 * <p>
	 * As result, {@code webDriver} must open given url and waits must be
	 * executed.
	 * </p>
	 */
	@Test
	public void testHandleWithoutHandlers() {
		System.out.println("testHandleWithoutHandlers");

		String url = "http://www.something.com/c/500.html?sortingOrder=ASC;JSESSIONID=1A530637289A03B07199A44E8D531427";

		bean.handle(url);

		verify(webDriver, times(1)).get(url);
		verify(waitUtil, times(1)).waitTime(0);
		verify(waitUtil, times(1)).ajaxJQuery(0, webDriver);
	}

	/**
	 * tests {@link DefaultPageCrawler#handle(String)} when two page handlers
	 * are available. The first one is not applicable, but second one is it.
	 * <p>
	 * As result, {@code webDriver} must open given url, waits must be executed
	 * and second pageHandler has to be called.
	 * </p>
	 */
	@Test
	public void testHandleWithHandlers() {
		System.out.println("testHandleWithoutHandlers");

		String url = "http://www.something.com/c/500.html?sortingOrder=ASC;JSESSIONID=1A530637289A03B07199A44E8D531427";
		bean.pageLoadAjaxSeconds = 5;
		bean.pageLoadWaitSeconds = 3;
		bean.pageHandlers = Arrays.asList(pageHandler, pageHandler2);
		when(pageHandler2.isApplicable(url)).thenReturn(true);

		bean.handle(url);

		verify(webDriver, times(1)).get(url);
		verify(waitUtil, times(1)).waitTime(3000);
		verify(waitUtil, times(1)).ajaxJQuery(5000, webDriver);
		verify(pageHandler2, times(1)).handle(url, webDriver);
	}

	/**
	 * tests {@link DefaultPageCrawler#handle(String)} when two page handlers
	 * are available. The first one is not applicable, but second one is it.
	 * Additionally pageMarkerHandler is set.
	 * <p>
	 * As result, {@code webDriver} must open given url, pageMarkerHandler must
	 * be called instead of waits.
	 * </p>
	 */
	@Test
	public void testHandleWithHandlersWithPageMarkerHandler() {
		System.out.println("testHandleWithHandlersWithPageMarkerHandler");

		String url = "http://www.something.com/c/500.html?sortingOrder=ASC;JSESSIONID=1A530637289A03B07199A44E8D531427";
		bean.pageLoadAjaxSeconds = 5;
		bean.pageLoadWaitSeconds = 3;
		bean.pageHandlers = Arrays.asList(pageHandler, pageHandler2);
		when(pageHandler2.isApplicable(url)).thenReturn(true);
		bean.pageMarkerHandler = pageMarkerHandler;

		bean.handle(url);

		verify(webDriver, times(1)).get(url);
		verify(pageMarkerHandler, times(1)).setVaniMarker(webDriver);
		verify(pageMarkerHandler, times(1)).waitUntilMarkerIsPresent(webDriver);
		verify(waitUtil, times(0)).waitTime(anyLong());
		verify(waitUtil, times(0)).ajaxJQuery(anyLong(), eq(webDriver));
		verify(pageHandler2, times(1)).handle(url, webDriver);
	}

	/**
	 * tests {@link DefaultPageCrawler#handle(String)} when 3 applicable urls
	 * are available, but one is duplicated.
	 * <p>
	 * As result, {@code webDriver} must open 2 urls and waits must be executed.
	 * </p>
	 */
	@Test
	public void testCrawl() {
		System.out.println("testCrawl");

		List<String> applicableUrls = new ArrayList<>(Arrays.asList(
				"http://www.something.com/c/500.html?sortingOrder=ASC;JSESSIONID=1A530637289A03B07199A44E8D531427",
				"www.something.com/c/500/document/922.pdf", "http://www.something.com/c/500.html?sortingOrder=ASC"));

		init(new DefaultPageCrawler() {
			@Override
			protected List<String> getApplicableUrls() {
				return new ArrayList<>(applicableUrls);
			}
		});

		bean.crawl();

		verify(webDriver, times(1)).get(applicableUrls.get(0));
		verify(webDriver, times(1)).get(applicableUrls.get(1));
	}

	/**
	 * tests {@link DefaultPageCrawler#handle(String)} when no applicable urls
	 * are available.
	 * <p>
	 * As result, nothing should be done.
	 * </p>
	 */
	@Test
	public void testCrawlWithoutApplicableUrls() {
		System.out.println("testCrawlWithoutApplicableUrls");

		List<String> applicableUrls = new ArrayList<>();

		init(new DefaultPageCrawler() {
			@Override
			protected List<String> getApplicableUrls() {
				return new ArrayList<>(applicableUrls);
			}
		});

		bean.crawl();

		verify(webDriver, times(0)).get(anyString());
	}

	/**
	 * tests {@link DefaultPageCrawler#start()}.
	 * <p>
	 * As result, {@link WebDriver} of {@link ApplicationContext} must be
	 * called.
	 * </p>
	 */
	@Test
	public void testStart() {
		System.out.println("testStart");

		List<String> applicableUrls = new ArrayList<>(Arrays.asList(
				"http://www.something.com/c/500.html?sortingOrder=ASC;JSESSIONID=1A530637289A03B07199A44E8D531427",
				"www.something.com/c/500/document/922.pdf", "http://www.something.com/c/500.html?sortingOrder=ASC"));

		init(new DefaultPageCrawler() {
			@Override
			protected List<String> getApplicableUrls() {
				return new ArrayList<>(applicableUrls);
			}
		});
		when(appContext.getBean(WebDriver.class)).thenReturn(webDriver2);

		bean.start();

		verify(webDriver2, times(1)).get(applicableUrls.get(0));
		verify(webDriver2, times(1)).get(applicableUrls.get(1));
	}

	/**
	 * tests {@link DefaultPageCrawler#start(WebDriver)}.
	 * <p>
	 * As result, provided {@link WebDriver} must be called.
	 * </p>
	 */
	@Test
	public void testStartWebDriver() {
		System.out.println("testStartWebDriver");

		List<String> applicableUrls = new ArrayList<>(Arrays.asList(
				"http://www.something.com/c/500.html?sortingOrder=ASC;JSESSIONID=1A530637289A03B07199A44E8D531427",
				"www.something.com/c/500/document/922.pdf", "http://www.something.com/c/500.html?sortingOrder=ASC"));

		init(new DefaultPageCrawler() {
			@Override
			protected List<String> getApplicableUrls() {
				return new ArrayList<>(applicableUrls);
			}
		});

		bean.start(webDriver2);

		verify(webDriver2, times(1)).get(applicableUrls.get(0));
		verify(webDriver2, times(1)).get(applicableUrls.get(1));
	}

	/**
	 * tests {@link DefaultPageCrawler#initializeHandlers()} when there are 2
	 * handlers.
	 * <p>
	 * As result, all detected handlers must be instantiated and its url
	 * patterns should be registered.
	 * </p>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testInitializeHandlers() {
		System.out.println("testInitializeHandlers");

		when(reflections.getTypesAnnotatedWith(org.markysoft.vani.core.annotation.PageHandler.class))
				.thenReturn(new HashSet(Arrays.asList(TestPageHandler.class, TestPageHandler2.class)));
		when(pageHandlerFactory.create(TestPageHandler.class)).thenReturn(pageHandler);
		when(pageHandlerFactory.create(TestPageHandler2.class)).thenReturn(pageHandler2);
		when(pageHandler2.getUrlPatterns()).thenReturn(new TreeSet<String>(Arrays.asList("/c/", "/c/*./document/")));
		when(pageHandler.getUrlPatterns()).thenReturn(new TreeSet<String>(Arrays.asList("/c/*./image/")));

		bean.initializeHandlers();

		verify(pageHandlerFactory, times(1)).create(TestPageHandler.class);
		verify(pageHandlerFactory, times(1)).create(TestPageHandler2.class);
		assertList("wrong urlPatterns: ", Arrays.asList("/c/*./image/", "/c/", "/c/*./document/"), bean.urlPatterns);
		assertList("wrong pageHandlers: ", Arrays.asList(pageHandler, pageHandler2), bean.pageHandlers);
	}

	protected void assertList(String message, List<?> expected, Collection<?> actual) {
		for (Object expectedObj : expected) {
			Assert.assertTrue(message + expectedObj + ": not found!", actual.contains(expectedObj));
		}
	}

	protected void init(DefaultPageCrawler bean) {
		this.bean = bean;
		bean.webDriver = webDriver;
		bean.vaniContext = vaniContext;
		bean.waitUtil = waitUtil;
		bean.pageHandlerFactory = pageHandlerFactory;
		bean.pageHandlers = new ArrayList<>();
		bean.linkUtils = linkUtils;

	}

	@org.markysoft.vani.core.annotation.PageHandler
	class TestPageHandler {
	}

	@org.markysoft.vani.core.annotation.PageHandler
	class TestPageHandler2 {
	}
}
