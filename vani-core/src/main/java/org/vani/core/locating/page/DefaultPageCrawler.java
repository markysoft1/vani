package org.vani.core.locating.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.vani.core.VaniContext;
import org.vani.core.annotation.UrlMapping;
import org.vani.core.locating.JQueryElement;
import org.vani.core.locating.locator.ByJQuery;
import org.vani.core.wait.WaitUtil;

/**
 * This implementation collects all classes annotated with
 * {@link org.vani.core.annotation.PageHandler} and use its declarations of
 * {@link UrlMapping} as applicable url patterns.
 * 
 * @author Thomas
 *
 */
public class DefaultPageCrawler implements PageCrawler {
	protected final Log logger = LogFactory.getLog(getClass());
	protected Set<String> urlPatterns = new HashSet<>();
	protected WebDriver webDriver;
	@Autowired
	protected VaniContext vaniContext;
	@Autowired
	protected PageHandlerFactory pageHandlerFactory;
	@Autowired
	protected WaitUtil waitUtil;
	@Value("${vani.pageCrawler.pageLoadWaitSeconds}")
	protected int pageLoadWaitSeconds;
	@Value("${vani.pageCrawler.pageLoadAjaxSeconds}")
	protected int pageLoadAjaxSeconds;
	protected List<PageHandler> pageHandlers = new ArrayList<>();

	/**
	 * This method looks for classes annotated with
	 * {@link org.vani.core.annotation.PageHandler}. The found classes will be
	 * instantiated by {@link PageHandlerFactory} and the declared url patterns
	 * are registered.
	 */
	@PostConstruct
	public void initializeHandlers() {
		Set<Class<?>> handlerClasses = vaniContext.getReflections()
				.getTypesAnnotatedWith(org.vani.core.annotation.PageHandler.class);
		for (Class<?> handlerClass : handlerClasses) {
			PageHandler<?> handler = pageHandlerFactory.create(handlerClass);
			urlPatterns.addAll(handler.getUrlPatterns());
			pageHandlers.add(handler);
		}
	}

	@Override
	public void start(WebDriver webDriver) {
		this.webDriver = webDriver;
		crawl();
	}

	@Override
	public void start() {
		this.webDriver = vaniContext.getAppContext().getBean(WebDriver.class);
		crawl();
	}

	/**
	 * This method does the crawling. It collects all applicable urls of current
	 * page and open each url. After that the opened page wil be searched for
	 * new applicable urls again. If no applicable urls exists, which are not
	 * already visited, method will be ended.
	 */
	protected void crawl() {
		Set<String> visitedUrls = new HashSet<>();
		List<String> urls = getApplicableUrls();
		while (!urls.isEmpty()) {
			String url = urls.remove(0);
			if (!isVisited(url, visitedUrls)) {
				visitedUrls.add(removeJSessionId(url));

				handle(url);

				urls.addAll(getApplicableUrls());
			}
		}
	}

	/**
	 * This method opens given url, executes waits and calls all applicable
	 * {@code PageHandler}.
	 * 
	 * @param url
	 */
	protected void handle(String url) {
		logger.debug("try to open next url '" + url + "'");
		webDriver.get(url);

		logger.debug("wait explicit '" + pageLoadWaitSeconds + "' seconds after page changed");
		waitUtil.waitTime(pageLoadWaitSeconds * 1000);

		logger.debug("wait max '" + pageLoadAjaxSeconds + "' seconds for ajax requests are finished!");
		waitUtil.ajaxJQuery(pageLoadAjaxSeconds * 1000, webDriver);

		for (PageHandler handler : pageHandlers) {
			if (handler.isApplicable(url)) {
				handler.handle(url, webDriver);
			}
		}
		logger.debug("handling url '" + url + "' finished");
	}

	/**
	 * This method will check whether provided url is in given
	 * {@code visitedUrls} set. If specified url contains a jSessionId, it will
	 * be remove before checking.
	 * 
	 * @param url
	 * @param visitedUrls
	 *            set of all visited urls
	 * @return {@code true} if given {@code visitedUrls} contains provided url
	 *         (without jSessionId), else {@code false}
	 */
	protected boolean isVisited(String url, Set<String> visitedUrls) {
		boolean result = false;

		url = removeJSessionId(url);
		result = visitedUrls.contains(url);
		return result;
	}

	/**
	 * This method removes the {@code JSESSIONID} from provided url. It checks
	 * uppercase and lowercase.
	 * 
	 * @param url
	 * @return returns url without {@code JSESSIONID} or provided url if it
	 *         doesn't contains the marker.
	 */
	protected String removeJSessionId(String url) {
		if (url.toLowerCase().contains(";jsessionid")) {
			String[] parts = url.split("(?i);jsessionid");

			if (parts.length > 1) {
				int idx = parts[1].indexOf("?");
				if (idx >= 0) {
					parts[1] = parts[1].substring(idx);
				} else {
					parts[1] = "";
				}
			}

			StringBuilder builder = new StringBuilder();
			for (String p : parts) {
				builder.append(p);
			}

			url = builder.toString();
		}
		return url;
	}

	/**
	 * This method look for all urls on current page, which matches the declared
	 * url patterns.
	 * 
	 * @return returns a list of href-properties of all found matches.
	 */
	protected List<String> getApplicableUrls() {
		logger.debug("look for applicable urls ...");
		List<String> result = new ArrayList<>();
		for (String pattern : urlPatterns) {
			logger.debug("try to find urls with pattern '" + pattern + "'...");
			JQueryElement element = new ByJQuery("a:regex(prop:href," + pattern + ")", vaniContext).find(webDriver);

			logger.debug("add matched urls for pattern '" + pattern + "' to queue");
			element.each(a -> result.add(a.prop("href")));
		}
		logger.debug("looking for applicable urls done");
		return result;
	}
}
