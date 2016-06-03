package org.markysoft.vani.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.GlobalReference;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JavaScriptFunction;
import org.markysoft.vani.core.annotation.JsFunctionArguments;
import org.markysoft.vani.core.annotation.JsFunctionName;
import org.markysoft.vani.core.annotation.ManualJavaScriptInterface;
import org.markysoft.vani.core.javascript.GlobalReferenceHolder;
import org.markysoft.vani.core.javascript.JavaScriptException;
import org.markysoft.vani.core.javascript.JavaScriptSource;
import org.markysoft.vani.core.javascript.TypeHandler;
import org.markysoft.vani.core.javascript.VaniJavaScriptExecutor;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.util.StringUtils;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * This class is responsible for executing the js function of call corresponding
 * method of js interface. It also prepare the argument list and call manual
 * implementation of methods.
 * 
 * @author Thomas
 *
 * @see JavaScript
 * @see JavaScriptFunction
 * @see ManualJavaScriptInterface
 */
public class JavaScriptMethodInterceptor {
	private final Log logger = LogFactory.getLog(getClass());
	private JavaScriptSource<?> jsSource;
	private VaniContext vaniContext;
	private List<Object> manualJSInterfaceImplemenations;

	public JavaScriptMethodInterceptor(JavaScriptSource<?> jsSource, VaniContext vaniContext,
			List<Object> manualJSInterfaceImplemenations) {
		this.vaniContext = vaniContext;
		this.jsSource = jsSource;
		this.manualJSInterfaceImplemenations = manualJSInterfaceImplemenations;
	}

	@RuntimeType
	public Object intercept(@AllArguments Object[] arguments, @Origin Method invokedMethod,
			@Origin Class<?> targetClass) {

		Object result = null;
		JavaScriptFunction jsFuncAnnotation = invokedMethod.getDeclaredAnnotation(JavaScriptFunction.class);
		if (jsFuncAnnotation != null) {
			result = invokeScript(invokedMethod, invokedMethod.getReturnType(), jsFuncAnnotation, arguments);
		} else {
			result = invokeManualImplementation(invokedMethod);
		}

		return result;
	}

	/**
	 * This method checks whether there is a type handler for corresponding
	 * return type and call it when exists.
	 * 
	 * @param result
	 * @param returnType
	 * @param webDriver
	 * @return returns provided {@code result} or converted value by registered
	 *         {@link TypeHandler}.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> T handleResult(Object result, Class<T> returnType, WebDriver webDriver) {
		TypeHandler typeHandler = vaniContext.getTypeHandlerFor(returnType);
		if (typeHandler != null) {
			result = typeHandler.get(result, webDriver);
		}
		return (T) result;
	}

	/**
	 * This method is responsible for preparing all for the call, execute js
	 * function and handle its result.
	 * 
	 * @param jsMethod
	 * @param returnType
	 * @param jsFunctionAnnotation
	 * @param args
	 * @return
	 */
	private Object invokeScript(Method jsMethod, Class<?> returnType, JavaScriptFunction jsFunctionAnnotation,
			Object[] args) {
		String methodName = jsMethod.getName();
		Map<Class<?>, Object> filteredArgs = new HashMap<>(10);
		args = filterArguments(jsMethod, args, filteredArgs, JavascriptExecutor.class, GlobalReferenceHolder.class);
		args = prepareArgumentsForCustomCallFunc(args, filteredArgs, methodName, jsFunctionAnnotation);
		VaniJavaScriptExecutor executor = getExecutor((JavascriptExecutor) filteredArgs.get(JavascriptExecutor.class));
		JavascriptExecutor wrappedExecutor = executor.getWrappedExecutor();
		WebDriver webDriver = null;
		if (wrappedExecutor instanceof WebDriver) {
			webDriver = (WebDriver) wrappedExecutor;
		}

		return handleResult(executor.execute(methodName, jsFunctionAnnotation, args), returnType, webDriver);
	}

	protected VaniJavaScriptExecutor getExecutor(JavascriptExecutor jsExecutor) {
		if (jsExecutor == null) {
			jsExecutor = (JavascriptExecutor) vaniContext.getAppContext().getBean(WebDriver.class);
		}
		return new VaniJavaScriptExecutor(jsExecutor, jsSource);
	}

	/**
	 * This method will extract all parameters, its class is assignable from
	 * provided filtering classes. So it reduces the provided array and add
	 * filtered entries to specified {@code filteredEntries} map. The key of the
	 * map will be the filtered class and <b>not</b> the class of parameter
	 * value.
	 * <p>
	 * If a parameter entry is {@code NULL}, it will extracts the desired type
	 * from corresponding js-method.
	 * 
	 * @param jsMethod
	 *            calling js-method
	 * @param arguments
	 *            arguments for calling js-method
	 * @param filteredEntries
	 *            map containing all filtered matches
	 * @param filters
	 *            array with filter classes
	 * @return returns the provided array without values filtered by specified
	 *         filters or {@code NULL} if no filters or arguments are available.
	 */
	protected Object[] filterArguments(Method jsMethod, Object[] arguments, Map<Class<?>, Object> filteredEntries,
			Class<?>... filters) {
		Object[] result = arguments;
		if (arguments != null && filters != null) {
			List<Object> jsArgs = new ArrayList<>(arguments.length);
			Class<?>[] parameters = jsMethod.getParameterTypes();
			for (int i = 0; i < arguments.length; i++) {
				Object arg = arguments[i];
				Class<?> param = parameters[i];
				boolean matching = false;
				if (arg == null || !(arg instanceof String)) {
					Class<?> filterValue = arg == null ? param : arg.getClass();
					for (Class<?> filter : filters) {
						if (filter.isAssignableFrom(filterValue)) {
							filteredEntries.put(filter, arg);
							matching = true;
						}
					}
				}
				if (!matching) {
					jsArgs.add(arg);
				}
			}
			result = jsArgs.toArray();
		}
		return result;
	}

	/**
	 * This method prepares the arguments array for using custom call function.
	 * If current {@link JavaScriptSource} has no call function, provided
	 * {@code arguments} array will be returned.
	 * <p>
	 * But if there is one, the returned array will only contain the required
	 * parameters for it. So this method is responsible for handling
	 * {@link JsFunctionArguments}, {@link JsFunctionName},
	 * {@link GlobalReference}.
	 * </p>
	 * <p>
	 * All others parameters will be ignored
	 * </p>
	 * 
	 * @param arguments
	 *            arguments of calling js function
	 * @param filteredArguments
	 * @param jsMethodName
	 *            name of called method of js interface
	 * @param jsFunctionAnnotation
	 *            annotation of called method of js-interface
	 * @return
	 */
	protected Object[] prepareArgumentsForCustomCallFunc(Object[] arguments, Map<Class<?>, Object> filteredArguments,
			String jsMethodName, JavaScriptFunction jsFunctionAnnotation) {
		Object[] result = arguments;
		if (jsSource.getJsCallFunction() != null) {
			List<Object> argList = new ArrayList<>(5);
			Method callFuncMethod = jsSource.getJsCallFunction().getCallMethod();
			Parameter[] params = callFuncMethod.getParameters();
			if (params != null) {
				for (Parameter param : params) {
					if (param.isAnnotationPresent(GlobalReference.class)) {
						GlobalReferenceHolder reference = (GlobalReferenceHolder) filteredArguments
								.get(GlobalReferenceHolder.class);
						String referenceValue = null;
						if (reference != null) {
							referenceValue = reference.getReference();
						}
						argList.add(referenceValue);
					} else if (param.isAnnotationPresent(JsFunctionName.class)) {
						String funcName = null;
						if (StringUtils.isEmpty(jsFunctionAnnotation.value())) {
							if (StringUtils.isEmpty(jsFunctionAnnotation.name())) {
								funcName = jsMethodName;
							} else {
								funcName = jsFunctionAnnotation.name();
							}

						}
						argList.add(funcName);
					} else if (param.isAnnotationPresent(JsFunctionArguments.class)) {
						argList.add(arguments);
					}
				}
			}
			result = argList.toArray();
		}
		return result;
	}

	/**
	 * This method calls the specified method with given arguments on
	 * corresponding manual implementation instance.
	 * 
	 * @param method
	 * @param arguments
	 * @return returns result of corresponding method of manual implementation
	 *         or {@code NULL} if method has {@code void} as return type.
	 * @throws JavaScriptException
	 */
	protected Object invokeManualImplementation(Method method, Object... arguments) throws JavaScriptException {
		Object result = null;
		for (Object impl : manualJSInterfaceImplemenations) {
			try {
				result = method.invoke(impl, arguments);
				break;
			} catch (IllegalArgumentException ex) {
				logger.debug("manual implementation '" + impl.getClass() + "' does not have appropriate method '"
						+ method + "':" + ex.getMessage());
			} catch (Exception ex) {
				throw new JavaScriptException(
						"Calling method '" + method + "' of manual implementation '" + impl + "' failed: ", ex);
			}
		}
		return result;
	}

}
