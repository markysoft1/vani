package org.vani.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.JavascriptExecutor;
import org.vani.core.javascript.JQuery;
import org.vani.core.javascript.JQueryRegexSelectorPlugin;
import org.vani.core.javascript.TypeHandler;
import org.vani.core.javascript.VaniUtils;

/**
 * This annotation marks an interface to handle it as javascript-interface and
 * bound it's methods to corresponding js-functions. The binding will be
 * automatically done by vani. So you only have to declare the interface and the
 * javascript source path.
 * <p>
 * <h3>Declaration of java source code</h3> You create a js-file for the
 * javascript code and tell vani the path to that file. This is done by
 * {@link JavaScript#source()} or {@link JavaScript#sources()} when you have
 * multiple files. The path could by delcared as filesystem lookup or classpath.
 * The classpath lookup also supports wildcards. For example the declaration of
 * {@link JQuery}, vani will look for a classpath resource with the name
 * {@code jquery-2.2.1.js}:
 * </p>
 * 
 * <pre>
 * <code>
 * &#64;JavaScript(source = "classpath:jquery-2.2.1.js")
 * public interface JQuery{
 * 	...
 * }
 * </code>
 * </pre>
 * <p>
 * The following declaration shows using multiple sources. The file
 * {@code my.js}, which path starts in the current working directory, will be
 * appended to the classpath resource {@code jquery-2.2.1.js}:
 * </p>
 * 
 * <pre>
 * <code>
 * &#64;JavaScript(sources = {"classpath:org/vani/javascript/jquery-2.2.1.js","website/scripts/my.js"})
 * public interface JQuery{
 * 	...
 * }
 * </code>
 * </pre>
 * <p>
 * <h3>Injection Process</h3> At context startup, all javascript sources will be
 * loaded. The injection into the page will be done before each method call of
 * the js-interface. If you don't want that, you have to declare a detection
 * script (see {@link DetectionScript}). This will be executed to check whether
 * the source must be injected or not.
 * </p>
 * <p>
 * After the execution in the browser (see {@link JavascriptExecutor} for more
 * details), the return value will be converted. If you need a custom handling
 * of this value, you can define a @{@link TypeHandler} with
 * {@link @JsTypeHandler}.
 * <p>
 * The call of your javascript function or code bound to a method of
 * js-interface will be done by a call function. You can define your own
 * function (see {@link JsCallFunction}).
 * </p>
 * <p>
 * <h3>Dependency</h3> A javascript can depends on other scripts. This means,
 * that these scripts must be injected into the page before the current one is
 * sent. For example, the jquery-interface depends on {@link VaniUtils}, because
 * for handling jquery elements, a uuid is used for caching. Therefore the
 * {@link VaniUtils#uuid4()} will be needed:
 * </p>
 * 
 * <pre>
 * <code>
 * &#64;JavaScriptDependency(VaniUtils.class)
 * &#64;JavaScript(source = "classpath:jquery-regex-selector.js")
 * public interface JQueryRegexSelectorPlugin {
 * 	...
 * } 
 * </code>
 * </pre>
 * <p>
 * <h3>Plugins</h3> You can extends the underlying javascript-source, if you
 * declare the interface by extending an plugin-interface. For example, the
 * jquery-interface is extending {@link JQueryRegexSelectorPlugin} to provide a
 * regex-selector:
 * </p>
 * 
 * <pre>
 * <code>
 * &#64;JavaScript(source = "classpath:jquery-regex-selector.js")
 * public interface JQueryRegexSelectorPlugin {
 * 	...
 * } 
 * </code>
 * </pre>
 * 
 * <pre>
 * <code>
 * &#64;JavaScript(source = "classpath:jquery-2.2.1.js")
 * public interface JQuery extends JQueryRegexSelectorPlugin {
 * 	...
 * }
 * </code>
 * </pre>
 * <p>
 * In the above example, the source code of {@link JQueryRegexSelectorPlugin}
 * will be append to the one of {@link JQuery}.
 * </p>
 * 
 * 
 * @author Thomas
 * @see JsCallFunction
 * @see JsTypeHandler
 * @see JavascriptExecutor
 * @see TypeHandler
 */
@Target({ ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JavaScript {
	String name() default "vaniJS";

	String[] sources() default {};

	String source() default "";
}
