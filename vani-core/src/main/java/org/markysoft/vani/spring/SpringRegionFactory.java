package org.markysoft.vani.spring;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.markysoft.vani.core.IllegalVaniFieldException;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.ContentCondition;
import org.markysoft.vani.core.annotation.Page;
import org.markysoft.vani.core.annotation.Xhr;
import org.markysoft.vani.core.locating.FragmentObject;
import org.markysoft.vani.core.locating.PageObject;
import org.markysoft.vani.core.locating.RegionObject;
import org.markysoft.vani.core.locating.UnableToLocateException;
import org.markysoft.vani.core.locating.UnresolvableLocatorException;
import org.markysoft.vani.core.locating.factory.LocatorBuilderFactory;
import org.markysoft.vani.core.locating.factory.RegionFactory;
import org.markysoft.vani.core.locating.locator.ByJQuery;
import org.markysoft.vani.core.locating.locator.FindByJQuery;
import org.markysoft.vani.core.locating.locator.JQueryLocatorBuilder;
import org.markysoft.vani.core.util.VaniReflectionUtil;
import org.markysoft.vani.core.util.XhrInterceptor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class SpringRegionFactory implements RegionFactory {
	private final Log logger = LogFactory.getLog(getClass());
	private ConfigurableListableBeanFactory configurableBeanFactory;
	@Autowired
	private LocatorBuilderFactory locatorBuilderFactory;
	@Autowired
	protected VaniReflectionUtil reflectionUtil;
	@Autowired
	protected VaniContext vaniContext;

	public SpringRegionFactory(ConfigurableListableBeanFactory bf) {
		configurableBeanFactory = bf;
	}

	@Override
	public <T> T create(Class<T> regionClass, WebElement rootElement) {
		return create(regionClass, null, null, null, rootElement);
	}

	@Override
	public <T> T create(Class<T> regionClass, WebDriver webDriver, WebElement rootElement) {
		return create(regionClass, webDriver, null, null, rootElement);
	}

	@Override
	public <T> T create(Class<T> regionClass, WebDriver webDriver) {
		return create(regionClass, webDriver, null, null, null);
	}

	@Override
	public <T> T create(Class<T> regionClass, WebDriver webDriver, Page pageAnnotation) {
		return create(regionClass, webDriver, pageAnnotation, null, null);
	}

	@Override
	public <T> T createPage(Class<T> regionClass, WebDriver webDriver, String pageUrl) {
		return create(regionClass, webDriver, null, pageUrl, null);
	}

	@Override
	public <T> T create(Class<T> regionClass, Page pageAnnotation) {
		return create(regionClass, null, pageAnnotation, null, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> regionClass, WebDriver webDriver, Page pageAnnotation, String pageUrl,
			WebElement rootElement) {
		Class<? extends T> targetType = regionClass;
		if (List.class.isAssignableFrom(regionClass)) {
			targetType = (Class<? extends T>) ArrayList.class;
		}
		if (FragmentObject.class.isAssignableFrom(regionClass)) {
			try {
				targetType = resolveFragmentClass(regionClass);
			} catch (Exception ex) {
				throw new UnableToLocateException("Cannot determine the fragment type for '" + regionClass + "': " + ex,
						ex);
			}
		}
		T result = null;
		if (reflectionUtil.hasMethodWithAnnotation(targetType, Xhr.class)) {
			result = createProxy(targetType, getWebDriver(pageAnnotation, webDriver));
		} else {
			try {
				result = targetType.newInstance();
			} catch (Exception ex) {
				throw new UnableToLocateException(
						"Cannot create new instance of region class '" + targetType + "': " + ex, ex);
			}
		}

		try {
			injectPreProcessingFields(result, pageAnnotation, pageUrl, webDriver, rootElement);
		} catch (Exception ex) {
			throw new UnableToLocateException("Cannot inject basic dependencies to class '" + targetType + "': " + ex,
					ex);
		}
		String beanName = StringUtils.uncapitalize(targetType.getSimpleName());
		result = (T) configurableBeanFactory.initializeBean(result, beanName);
		configurableBeanFactory.autowireBeanProperties(result, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
		return result;
	}

	@SuppressWarnings("unchecked")
	protected <T> void injectPreProcessingFields(T beanInstance, Page pageAnnotation, String pageUrl,
			WebDriver webDriver, WebElement rootElement) throws IllegalVaniFieldException, IllegalAccessException {
		if (beanInstance instanceof RegionObject) {
			reflectionUtil.setFieldValue("vaniContext", beanInstance, vaniContext);
		}

		webDriver = getWebDriver(pageAnnotation, webDriver);
		if (webDriver != null) {
			reflectionUtil.setFieldValue("webDriver", beanInstance, webDriver);
		}

		if (pageAnnotation != null && !StringUtils.isEmpty(pageUrl)) {
			pageUrl = pageAnnotation.url();
		}
		if (beanInstance instanceof PageObject) {
			if (!StringUtils.isEmpty(pageUrl)) {
				reflectionUtil.setFieldValue("pageUrl", beanInstance, pageUrl);
			}
		}

		if (beanInstance instanceof FragmentObject) {
			if (rootElement == null) {
				try {
					rootElement = resolveRootElement((Class<? extends FragmentObject>) beanInstance.getClass(),
							webDriver);
				} catch (Exception ex) {
					throw new UnableToLocateException("cannot locate root element for class '"
							+ beanInstance.getClass().getName() + "': " + ex.getMessage(), ex);
				}
			}
			if (rootElement != null) {
				try {
					Field rootElementField = reflectionUtil.getField("rootElement", beanInstance.getClass());
					if (rootElementField != null) {
						ReflectionUtils.makeAccessible(rootElementField);
						rootElementField.set(beanInstance, rootElement);
					}
				} catch (Exception ex) {
					throw new UnableToLocateException("cannot inject root element in class '"
							+ beanInstance.getClass().getName() + "': " + ex.getMessage(), ex);
				}
			}
		}
	}

	protected <T extends FragmentObject> WebElement resolveRootElement(Class<T> regionClass, WebDriver webDriver) {
		WebElement result = null;
		FindByJQuery findBy = reflectionUtil.getTypeAnnotation(FindByJQuery.class, regionClass);
		if (findBy != null) {
			JQueryLocatorBuilder builder = (JQueryLocatorBuilder) locatorBuilderFactory.get(JQueryLocatorBuilder.class);
			ByJQuery by = builder.build(findBy);

			result = by.find(webDriver);
		}
		return result;
	}

	protected <T> Class<? extends T> resolveFragmentClass(Class<T> regionClass) {
		Class<? extends T> result = null;

		Class<? extends T> fallback = null;
		Set<Class<? extends T>> subTypes = vaniContext.getReflections().getSubTypesOf(regionClass);
		for (Class<? extends T> type : subTypes) {
			if (Modifier.isAbstract(type.getModifiers())) {
				continue;
			}

			ContentCondition contentCondition = type.getAnnotation(ContentCondition.class);
			if (contentCondition == null) {
				fallback = type;
			} else {
				String selector = vaniContext.resolveExpression(contentCondition.value());
				ByJQuery by = new ByJQuery(selector, vaniContext);
				if (by.find().hasMatches()) {
					result = type;
					break;
				}
			}
		}
		if (result == null && fallback != null) {
			result = fallback;
		}

		if (result == null) {
			result = regionClass;
		}
		return result;
	}

	/**
	 * Method to resolve the correct WebDriver instance.<br>
	 * The lookup for instance is done in following order:<br>
	 * <ol>
	 * <li>bean name of webDriver bean specified by given pageAnnotation</li>
	 * <li>{@code fallbackDriver}</li>
	 * <li>instance lookup for {@link WebDriver}-class in spring context</li>
	 * <li>create default {@link WebDriver} instance with
	 * {@link VaniContext#createDefaultDriver()}</li>
	 * </ol>
	 * 
	 * @param pageAnnotation
	 * @param fallbackDriver
	 * @return returns resolved instance or created default {@link WebDriver}
	 */
	protected WebDriver getWebDriver(Page pageAnnotation, WebDriver fallbackDriver) {
		WebDriver result = null;
		if (pageAnnotation != null) {
			String driverBeanName = pageAnnotation.driverName();
			if (!StringUtils.isEmpty(driverBeanName)) {
				result = vaniContext.getAppContext().getBean(WebDriver.class, driverBeanName);
			}
		}
		if (result == null) {
			if (fallbackDriver != null) {
				result = fallbackDriver;
			} else {
				try {
					result = vaniContext.getAppContext().getBean(WebDriver.class);
				} catch (NoSuchBeanDefinitionException ex) {
					logger.info("no webDriver found - create default web driver");
				}
			}
			if (result == null) {
				result = vaniContext.createDefaultDriver();
			}
		}

		return result;
	}

	/**
	 * method to create a proxy object for given class which intercepts methods
	 * annotated with {@link Xhr}.
	 * 
	 * @param targetClass
	 * @return
	 * @throws UnableToLocateException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <R> R createProxy(Class<R> targetClass, WebDriver webDriver) throws UnresolvableLocatorException {
		R result;
		try {
			//@formatter:off
			result = new ByteBuddy()
					.subclass(targetClass)
					.method(ElementMatchers.isAnnotatedWith(Xhr.class))
					.intercept(MethodDelegation.to(new XhrInterceptor(vaniContext,webDriver)))
					.make()
					.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded()
					.newInstance();
			//@formatter:on
		} catch (Exception ex) {
			throw new UnableToLocateException("cannot instantiate proxy for '" + targetClass + "': " + ex, ex);
		}
		return result;
	}
}
