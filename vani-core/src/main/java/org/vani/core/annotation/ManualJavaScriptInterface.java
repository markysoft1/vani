package org.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation tells vani, that an annotated interface is a manual
 * js-interface. This means, that you can add additional methods to an
 * js-interface, which implementation is declared in a normal class. For
 * example:
 * 
 * <pre>
 * <code>
 * &#64;ManualJavaScriptInterface
 * public interface ManualSomething{
 * 	String getSecret();
 * }
 * 
 * public class SomethingImpl implements ManualSomething{
 * 	&#64;Autowired
 * 	private WebDriver webDriver;
 * 
 * 	public String getSecret(){
 * 		...
 * 	}
 * }
 * 
 * &#64;JavaScript(...)
 * public interface Something extends ManualSomething{
 * }
 * 
 * </code>
 * </pre>
 * 
 * @author Thomas
 *
 */
@Target({ ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ManualJavaScriptInterface {
}
