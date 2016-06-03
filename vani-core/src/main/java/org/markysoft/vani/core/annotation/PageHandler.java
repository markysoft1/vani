package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.markysoft.vani.core.locating.FragmentObject;
import org.markysoft.vani.core.locating.PageObject;
import org.markysoft.vani.core.locating.page.PageCrawler;
import org.openqa.selenium.WebDriver;

/**
 * It declares a page handler for the page crawling mechanism. The page handler
 * is only relevant if you use the {@link PageCrawler}.
 * <h3>Using</h3>
 * <p>
 * To use the page crawling feature, you have to inject the {@link PageCrawler}
 * into your test class and call {@code start}-method.
 * </p>
 * <p>
 * However, you must declare at least one page handler inclusive url mapping.
 * The following example declares a handler, which {@code article}-method is
 * applicable for all urls containing {@code master-blog.com/article/}.
 * Additionally, the handler method gets an instance of {@code ArticlePage} as
 * parameter:
 * </p>
 * 
 * <pre>
 * <code>
 * &#64;PageHandler
 * &#64;UrlMapping("master-blog.com")
 * public class BlogPageHandler {
 * 
 * 	&#64;UrlMapping("/article/")
 * 	public void article(ArticlePage articlePage) {
 * 		...
 * 	}
 * }
 * </code>
 * </pre>
 * 
 * <h3>Handler method parameters</h3>
 * <p>
 * Vani can inject automatically {@link PageObject}, {@link FragmentObject},
 * using {@link WebDriver} or current page url, if you want. To do that, you
 * only have to specify the desired type as parameter of handler method like in
 * the above example. <b>The current url is always used as the first string
 * parameter.</b>
 * </p>
 * 
 * @author Thomas
 *
 */
@Target({ ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface PageHandler {
}
