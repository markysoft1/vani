package org.markysoft.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation declares a dependency of a js-interface. So the referenced
 * interface will be also loaded during executing a javascript call. The
 * dependency code is always placed before the annotated js-interface's code.
 * <p>
 * A dependency can also have dependencies, custom call function (see
 * {@link JsCallFunction} or a detection script (see {@link DetectionScript}.
 * Here is a examples:
 * </p>
 * 
 * <pre>
 * <code>
 * &#64;JavaScriptDependency({ VaniUtils.class, XhrTracking.class })
 * &#64;JavaScript(source = "classpath:jquery-2.2.1.js")
 * public interface JQuery {
 * 	...
 * }
 * </code>
 * </pre>
 * 
 * <pre>
 * <code>
 * &#64;JavaScript(source = "classpath:org/vani/javascript/vani-utils.js")
 * public interface VaniUtils {
 *	&#64;JavaScriptFunction(name = "window.vani.uuid4")
 *	String uuid4();
 *
 *	&#64;DetectionScript("window.vani !== undefined")
 *	public boolean isAvailable();
 * }
 * </code>
 * </pre>
 * 
 * @author Thomas
 * 
 * @see JsCallFunction
 * @see JavaScript
 * @see DetectionScript
 */
@Target({ ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JavaScriptDependency {
	/** reference to depending js-interfaces */
	Class<?>[] value();
}
