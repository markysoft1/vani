package org.markysoft.vani.core.javascript;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.markysoft.vani.core.annotation.DetectionScript;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JavaScriptFunction;
import org.markysoft.vani.core.annotation.JsCallFunction;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.util.StringUtils;

/**
 * This class is a wrapper for the using {@link JavascriptExecutor}. It prepares
 * the source, which is sent to the browser. This means that all dependencies,
 * js-interface's sources and plugins are concatenated if it's necessary.
 * 
 * @author Thomas
 * @see JavaScriptCallFunction
 * @see JsCallFunction
 * @see JavaScript
 * @see JavaScriptFunction
 */
public class VaniJavaScriptExecutor implements JavascriptExecutor {
	protected static final String BOUND_CALL = "return %s.apply(%s,arguments);";
	protected static final String CALL = "try{%s}catch(ex){console.log('Failed to execute injected script: '+ex);return '@JS-ERROR: '+ex;}";
	protected final Log logger = LogFactory.getLog(getClass());
	protected JavascriptExecutor jsExecutor;
	protected JavaScriptSource<?> jsSource;

	public VaniJavaScriptExecutor(JavascriptExecutor jsExecutor, JavaScriptSource<?> jsSource) {
		this.jsExecutor = jsExecutor;
		this.jsSource = jsSource;
	}

	@Override
	public Object executeScript(String script, Object... args) {
		return doExecute(prepareSource(script), args);
	}

	@Override
	public Object executeAsyncScript(String script, Object... args) {
		return doExecuteAsync(prepareSource(script), args);
	}

	/**
	 * Executes JavaScript in the context of the currently selected frame or
	 * window. The script fragment provided will be executed as the body of an
	 * anonymous function.
	 *
	 * <p>
	 * Within the script, use <code>document</code> to refer to the current
	 * document. Note that local variables will not be available once the script
	 * has finished executing, though global variables will persist.
	 * </p>
	 * <p>
	 * If the script has a return value (i.e. if the script contains a
	 * <code>return</code> statement), then the following steps will be taken:
	 * </p>
	 * <ul>
	 * <li>For an HTML element, this method returns a WebElement</li>
	 * <li>For a decimal, a Double is returned</li>
	 * <li>For a non-decimal number, a Long is returned</li>
	 * <li>For a boolean, a Boolean is returned</li>
	 * <li>For all other cases, a String is returned.</li>
	 * <li>For an array, return a List&lt;Object&gt; with each object following
	 * the rules above. We support nested lists.</li>
	 * <li>Unless the value is null or there is no return value, in which null
	 * is returned</li>
	 * </ul>
	 *
	 * <p>
	 * Arguments must be a number, a boolean, a String, WebElement, or a List of
	 * any combination of the above. An exception will be thrown if the
	 * arguments do not meet these criteria. The arguments will be made
	 * available to the JavaScript via the "arguments" magic variable, as if the
	 * function were called via "Function.apply"
	 * </p>
	 * 
	 * @param methodName
	 *            name of calling js-inteface method
	 * @param jsFunctionAnnotation
	 *            annotation of calling js-interface method
	 * @param args
	 *            The arguments to the script. May be empty
	 * @return One of Boolean, Long, String, List or WebElement or custom type.
	 *         Or null.
	 * @see {@link VaniJavaScriptExecutor#CALL} for wrapping call source
	 */
	public Object execute(String methodName, JavaScriptFunction jsFunctionAnnotation, Object... args) {
		try {
			String callSource = "";
			if (StringUtils.isEmpty(jsFunctionAnnotation.value())) {
				String funcName = "";
				if (StringUtils.isEmpty(jsFunctionAnnotation.name())) {
					funcName = methodName;
				} else {
					funcName = jsFunctionAnnotation.name();
				}
				callSource = getCallSource(funcName);
			} else {
				callSource = jsFunctionAnnotation.value();
			}

			callSource = String.format(CALL, callSource);

			String source = prepareSource(callSource);
			return doExecute(source, args);
		} catch (Exception ex) {
			throw new JavaScriptException("Exception occurred during executing script bound to method '" + methodName
					+ "' of interface '" + jsSource.getJsInterface() + "': " + ex.getMessage(), ex);
		}
	}

	/**
	 * This method builds the call source. This will be used to call a function
	 * of the associated source of js-interface. If the js-interface declares no
	 * custom call function (see {@link JsCallFunction}), the function will be
	 * called by {@code apply}-function.
	 * 
	 * @param funcName
	 *            name of javascript function
	 * @return returns the default call function if no custom one is declared,
	 *         else custom one will be returned.
	 * @see {@link VaniJavaScriptExecutor#BOUND_CALL} (default call source)
	 */
	protected String getCallSource(String funcName) {
		String result = "";
		if (jsSource.getJsCallFunction() == null) {
			result = String.format(BOUND_CALL, funcName, "null");
		} else {
			JavaScriptCallFunction jsCallFunction = jsSource.getJsCallFunction();
			StringBuilder callBuilder = new StringBuilder();
			String callFuncVariable = String.format("vaniJsCallFunc_%s", funcName);
			callBuilder.append(String.format("var %s = %s;", callFuncVariable, jsCallFunction.getCallFunctionSource()));
			callBuilder.append(String.format("return %s.apply(null,arguments);", callFuncVariable));
			result = callBuilder.toString();
		}
		return result;
	}

	/**
	 * This method is only wrapper for
	 * {@link JavascriptExecutor#executeScript(String, Object...)} and check
	 * whether there was a js-error.
	 * 
	 * @param script
	 *            script code which should be executed
	 * @param args
	 *            The arguments to the script. May be empty.
	 * @return returns the result of executed javascript.
	 * @throws JavaScriptException
	 *             will be thrown, when provided argument is a string starting
	 *             with js-error key
	 */
	protected Object doExecute(String script, Object... args) {
		try {
			return handleResult(jsExecutor.executeScript(script, args));
		} catch (Exception ex) {
			logger.debug("execution failed for script:\n" + script);
			throw ex;
		}
	}

	/**
	 * This method is only wrapper for
	 * {@link JavascriptExecutor#executeAsyncScript(String, Object...)} and
	 * check whether there was a js-error.
	 * 
	 * @param script
	 *            script code which should be executed
	 * @param args
	 *            The arguments to the script. May be empty.
	 * @return returns the result of executed javascript.
	 * @throws JavaScriptException
	 *             will be thrown, when provided argument is a string starting
	 *             with js-error key
	 */
	protected Object doExecuteAsync(String script, Object... args) throws JavaScriptException {
		try {
			return handleResult(jsExecutor.executeAsyncScript(script, args));
		} catch (Exception ex) {
			logger.debug("execution failed for script:\n" + script);
			throw ex;
		}
	}

	/**
	 * This method checks whether the javascript execution returned an string
	 * starting with js-error key. This key tell vani, that an error is occurred
	 * during script execution. If this is true, a {@link JavaScriptException}
	 * will be thrown, which contains the returns script error.
	 * 
	 * @param result
	 *            result of the javascript execution
	 * @return returns the provided argument, only if it is not a string
	 *         starting with js-error key.
	 * @throws JavaScriptException
	 *             will be thrown, when provided argument is a string starting
	 *             with js-error key
	 */
	protected Object handleResult(Object result) throws JavaScriptException {
		if (result != null && result instanceof String && ((String) result).startsWith("@JS-ERROR:")) {
			throw new JavaScriptException("Failed to execute injected script: " + result);
		}
		return result;
	}

	/**
	 * This method will prepare source code, which will be sent to browser. The
	 * result also includes the code of the corresponding js-interface inclusive
	 * all dependencies and plugins, but only if it's necessary.
	 * 
	 * @param source
	 *            source of bound function
	 * @return returns the source code, which will be sent to browser.
	 * @throws JavaScriptException
	 *             will be thrown if an error occurred during checking whether
	 *             script must be injected.
	 * @see {@link VaniJavaScriptExecutor#appendSource(StringBuilder, JavaScriptSource, Set)}
	 */
	protected String prepareSource(String source) throws JavaScriptException {
		StringBuilder sourceBuilder = new StringBuilder();

		appendSource(sourceBuilder, jsSource, new HashSet<JavaScriptSource<?>>());
		if (!StringUtils.isEmpty(source)) {
			sourceBuilder.append(source);
		}

		return sourceBuilder.toString();
	}

	/**
	 * This method resolves the source code for specified
	 * {@link JavaScriptSource} and set it to provided {@link StringBuilder}.The
	 * {@code builder} will contains following things at the end in this order:
	 * <ul>
	 * <li><b>dependencies</b>, which are not already available (see
	 * {@link DetectionScript}) or already appended to the {@code builder}. If a
	 * dependency also declares a dependency or plugin it will be handled in
	 * same order.</li>
	 * <li><b>source of js-interface</b> If source is already available (see
	 * {@link DetectionScript}), it will be skipped.</li>
	 * <li><b>plugin</b>, which are not already appended to the {@code builder}.
	 * </li>
	 * </ul>
	 * 
	 * @param builder
	 * @param jsSource
	 * @param processingSources
	 * @throws JavaScriptException
	 *             will be thrown, when an error occurred during checking
	 *             whether script is already available.
	 */
	protected void appendSource(StringBuilder builder, JavaScriptSource<?> jsSource,
			Set<JavaScriptSource<?>> processingSources) throws JavaScriptException {
		if (jsSource != null && !processingSources.contains(jsSource)) {
			processingSources.add(jsSource);
			for (JavaScriptSource<?> dependency : jsSource.getDependencies()) {
				appendSource(builder, dependency, processingSources);
			}
			boolean mustInject = false;
			try {
				mustInject = mustInject(jsSource);
			} catch (Exception ex) {
				throw new JavaScriptException("Cannot execute detection script: " + ex.getMessage(), ex);
			}
			if (mustInject) {
				builder.append(jsSource.getSource());
				builder.append("\n");
			}

			for (JavaScriptSource<?> plugin : jsSource.getPlugins()) {
				appendSource(builder, plugin, processingSources);
			}
		}
	}

	/**
	 * This method checks whether provided {@link JavaScriptSource} declares a
	 * detection script. If there is a detection script, it will be executed and
	 * its return value will be inverted and returned.
	 * 
	 * @param jsSource
	 * @return returns true, if no detection script is available or the declared
	 *         detection script returns false, else false will be returned
	 */
	protected boolean mustInject(JavaScriptSource<?> jsSource) {
		boolean result = true;
		DetectionScript detectionAnnotation = jsSource.getDetectionScriptAnnotation();
		if (detectionAnnotation != null) {
			String detectionScript = detectionAnnotation.value();
			if (!StringUtils.isEmpty(detectionScript)) {
				String statementPrefix = detectionAnnotation.autoReturn() ? "return " : "";
				String statementPostfix = detectionAnnotation.autoReturn() ? ";" : "";
				result = !((Boolean) doExecute(
						String.format("%s%s%s", statementPrefix, detectionScript, statementPostfix)));
			}
		}
		return result;
	}

	public JavascriptExecutor getWrappedExecutor() {
		return jsExecutor;
	}
}
