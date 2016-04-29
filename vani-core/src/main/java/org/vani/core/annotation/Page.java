package org.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.WebDriver;
import org.vani.core.locating.PageObject;

/**
 * Marks a field as page object, which should be injected by vani.<br>
 * Usage:
 * 
 * <pre>
 * &#64;RunWith(SpringJUnit4ClassRunner.class)
 * public class ContactTest{
 * 	&#64;Page(url = "www.my-app.com/contact.html")
 * 	private ContactPage contactPage;
 * 
 * ...
 * }
 * </pre>
 * 
 * <h3>Placeholder support</h3> It is also possible to use spring expresion, for
 * example:
 * 
 * <pre>
 * &#64;RunWith(SpringJUnit4ClassRunner.class)
 * public class ContactTest{
 * 	&#64;Page(url = "${appUrl}/contact.html")
 * 	private ContactPage contactPage;
 * 
 * ...
 * }
 * </pre>
 * 
 * <h3>Multiple WebDriver instances</h3> If you use multiple driver instances,
 * you can specify which driver should be used. For example:
 * 
 * <pre>
 * &#64;RunWith(SpringJUnit4ClassRunner.class)
 * public class ContactTest{
 * 	&#64;Page(driverName = "firefoxDriver")
 * 	private ContactPage contactPage;
 * 
 * ...
 * }
 * </pre>
 * 
 * @author Thomas
 *
 */
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Page {
	/**
	 * url which should be called for showing corresponding page. If you don't
	 * provide an url, the {@link PageUrl} annotation of corresponding
	 * {@link PageObject} implementation will be used to get the target url.
	 */
	String url() default "";

	/**
	 * {@code beanName} of {@link WebDriver}-instance, which should handle the
	 * corresponding object
	 */
	String driverName() default "";
}
