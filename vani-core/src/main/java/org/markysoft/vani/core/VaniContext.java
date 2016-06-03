package org.markysoft.vani.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JavaScriptDependency;
import org.markysoft.vani.core.annotation.JsTypeHandler;
import org.markysoft.vani.core.javascript.JavaScriptLoader;
import org.markysoft.vani.core.javascript.JavaScriptSource;
import org.markysoft.vani.core.javascript.TypeHandler;
import org.markysoft.vani.core.locating.factory.JavaScriptProxyFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.reflections.Reflections;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * This class is the central utility for the framework. It provides various
 * methods to interact with spring framework and is responsible for initialising
 * JavaScript interfaces.
 * 
 * @author Thomas
 *
 */
public class VaniContext {
	private final Log logger = LogFactory.getLog(getClass());
	private Reflections reflections;
	private Environment environment;
	private ApplicationContext appContext;
	private ConfigurableListableBeanFactory configurableBeanFactory;
	private JavaScriptLoader jsLoader;
	private JavaScriptProxyFactory jsProxyFactory;
	@SuppressWarnings("rawtypes")
	private Map<Class<?>, TypeHandler> typeHandlerRegistry = new HashMap<>(6);

	@Value("${vani.firefoxDriver.xpi:}")
	private String firefoxDriverXpi;

	public Reflections getReflections() {
		return reflections;
	}

	public void setReflections(Reflections reflections) {
		this.reflections = reflections;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}

	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	public void setConfigurableBeanFactory(ConfigurableListableBeanFactory configurableBeanFactory) {
		this.configurableBeanFactory = configurableBeanFactory;
	}

	/**
	 * method to check whether at least one profile of provided profile
	 * annotation is active.
	 * 
	 * @param profileAnnotation
	 * @return returns true if at least one profile is active, else false. If
	 *         {@code profileAnnotation} is {@code NULL} false will be returned.
	 */
	public boolean isProfileEnabled(Profile profileAnnotation) {
		boolean result = false;
		if (profileAnnotation != null) {
			String[] activeProfiles = environment.getActiveProfiles();
			if (activeProfiles != null) {
				List<String> activeProfilesList = Arrays.asList(activeProfiles);
				for (String profile : profileAnnotation.value()) {
					if (activeProfilesList.contains(profile)) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * method tries to resolve given SpEl-expression.
	 * 
	 * @param expr
	 *            expression with SpEl-expressions
	 * @return returns resolved value or the given one.
	 */
	public String resolveExpression(String expr) {
		String result = configurableBeanFactory.resolveEmbeddedValue(expr);
		return result;
	}

	/**
	 * method to generate and register {@link FirefoxDriver}.
	 * 
	 * @return returns default web driver instance ({@link FirefoxDriver})
	 */
	public WebDriver createDefaultDriver() {
		if (!StringUtils.isEmpty(firefoxDriverXpi)) {
			System.setProperty(FirefoxDriver.SystemProperty.DRIVER_XPI_PROPERTY, firefoxDriverXpi);
		}
		WebDriver driver = new FirefoxDriver();

		configurableBeanFactory.registerSingleton("firefoxDriver", driver);
		return driver;
	}

	/**
	 * <p>
	 * This method will scan classpath for all classes annotated with
	 * {@link JavaScript}.The corresponding source of each found interface will
	 * be loaded and the dependencies (specified by {@link JavaScriptDependency}
	 * and plugins will also be assigned.
	 * </p>
	 * <p>
	 * For each js-interface, a proxy will be created and registered as
	 * singelton spring bean. The registered bean name is equal to uncapitalized
	 * class name.
	 * </p>
	 * <p>
	 * After that, all available {@link TypeHandler} annotated with
	 * {@link JsTypeHandler} are registered.
	 * </p>
	 * 
	 */
	@PostConstruct
	protected void registerJavaScripts() {
		Map<Class<?>, JavaScriptSource<?>> jsSourceMap = new HashMap<>(10);
		Set<Class<?>> jsClasses = reflections.getTypesAnnotatedWith(JavaScript.class);
		if (jsClasses != null) {
			for (Class<?> jsClass : jsClasses) {
				JavaScript jsAnnotation = jsClass.getDeclaredAnnotation(JavaScript.class);
				jsSourceMap.put(jsClass, jsLoader.load(jsAnnotation, jsClass, this));
			}

			for (Class<?> jsClass : jsSourceMap.keySet()) {
				JavaScriptSource<?> jsSource = jsSourceMap.get(jsClass);
				JavaScriptDependency dependencyAnnotation = jsClass.getDeclaredAnnotation(JavaScriptDependency.class);
				if (dependencyAnnotation != null && dependencyAnnotation.value() != null) {
					for (Class<?> cls : dependencyAnnotation.value()) {
						JavaScriptSource<?> dependency = jsSourceMap.get(cls);
						if (dependency != null) {
							jsSource.addDependency(dependency);
						}
					}
				}
				Class<?>[] interfaces = jsClass.getInterfaces();
				if (interfaces != null) {
					for (Class<?> interfaceClass : interfaces) {
						if (interfaceClass.isAnnotationPresent(JavaScript.class)) {
							JavaScriptSource<?> plugin = jsSourceMap.get(interfaceClass);
							if (plugin != null) {
								jsSource.addPlugin(plugin);
							}
						}
					}
				}

				Object jsProxy = jsProxyFactory.createProxy(jsSource);
				configurableBeanFactory.registerSingleton(StringUtils.uncapitalize(jsClass.getSimpleName()), jsProxy);
			}
		}
		initJsTypeHandler();
	}

	/**
	 * This method will look for all classes annotated with
	 * {@link JsTypeHandler} and instantiates and registers found classes to
	 * {@link VaniContext}.
	 * <p>
	 * The dependencies of the handler instance will also be resolved by spring
	 * during instantiation.
	 * </p>
	 * 
	 */
	protected void initJsTypeHandler() {
		Set<Class<?>> handlerClasses = reflections.getTypesAnnotatedWith(JsTypeHandler.class);
		for (Class<?> handlerClass : handlerClasses) {
			TypeHandler<?, ?> handler = (TypeHandler<?, ?>) createBean(handlerClass);
			registerTypeHandler(handler);
		}
	}

	/**
	 * method to instantiate specified class and resolve all annotated
	 * dependencies. The created instance will <b>NOT<b> be registered.
	 * 
	 * @param beanClass
	 * @return
	 */
	public <T> T createBean(Class<T> beanClass) {
		T result = null;
		String beanName = StringUtils.uncapitalize(beanClass.getSimpleName());
		Object instance = null;
		logger.info("Creating new bean named '" + beanName + "'.");

		try {
			result = beanClass.newInstance();
		} catch (Exception ex) {
			throw new BeanInstantiationException(beanClass, ex.getMessage(), ex);
		}

		instance = configurableBeanFactory.initializeBean(result, beanName);
		configurableBeanFactory.autowireBeanProperties(instance, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
		logger.info("Bean named '" + beanName + "' created successfully.");

		return result;
	}

	/**
	 * This method will register provided {@link TypeHandler} to
	 * {@link VaniContext}.
	 * <p>
	 * If there is already a registered handler for the target type of given
	 * handler, it will be replaced with specified one.
	 * </p>
	 * 
	 * @param typeHandler
	 *            {@link TypeHandler} for registration (if you provide
	 *            {@code NULL}, value will be skipped)
	 */
	@SuppressWarnings("rawtypes")
	public void registerTypeHandler(TypeHandler typeHandler) {
		if (typeHandler != null) {
			typeHandlerRegistry.put(typeHandler.getTargetType(), typeHandler);
		}
	}

	/**
	 * This method will look for a registered {@link TypeHandler}, which is able
	 * to handle provided type.
	 * 
	 * @param targetType
	 *            type, which should be handled
	 * @return returns registered {@link TypeHandler} for provided type or
	 *         {@code NULL} if no appropriate handler is registered.
	 */
	@SuppressWarnings("unchecked")
	public <T> TypeHandler<T, ?> getTypeHandlerFor(Class<T> targetType) {
		return typeHandlerRegistry.get(targetType);
	}

	@PreDestroy
	public void shutdownWebDrivers() {
		Map<String, WebDriver> webDrivers = appContext.getBeansOfType(WebDriver.class);
		for (WebDriver webDriver : webDrivers.values()) {
			try {
				webDriver.quit();
			} catch (Exception ex) {
				logger.warn("cannot close webDriver '" + webDriver + "': " + webDriver);
			}
		}
	}

	@Autowired
	public void setJsLoader(JavaScriptLoader jsLoader) {
		this.jsLoader = jsLoader;
	}

	@Autowired
	public void setJsProxyFactory(JavaScriptProxyFactory jsProxyFactory) {
		this.jsProxyFactory = jsProxyFactory;
	}

}
