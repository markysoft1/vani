package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * It declares an url mapping for vani's page crawling mechanism. It's only work
 * in conjunction with {@link PageHandler}.
 * <p>
 * The annotation can be used for methods or types. If a class is annotated with
 * it, the specified pattern will define a prefix for all annotated method
 * mappings. The following example shows a handler, which is applicable for all
 * urls containing {@code master-blog.com/article/}:
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
 * 
 *	&#64;UrlMapping("${url.article.comment}")
 * 	public void comment(ArticlePage articlePage,CommentFragment commentFragment) {
 * 		...
 * 	}
 * }
 * </code>
 * </pre>
 * <p>
 * You can also use spring placeholders as value like in the above example.
 * </p>
 * 
 * @author Thomas
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlMapping {

	String value();
}
