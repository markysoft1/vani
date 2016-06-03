package org.markysoft.vani.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.markysoft.vani.core.VaniContext;
import org.markysoft.vani.core.annotation.LocatorBuilderClass;
import org.markysoft.vani.core.locating.FragmentObject;
import org.markysoft.vani.core.locating.LocatorBuilder;
import org.markysoft.vani.core.locating.RegionObject;
import org.markysoft.vani.core.locating.UnableToLocateException;
import org.markysoft.vani.core.locating.UnresolvableLocatorException;
import org.markysoft.vani.core.locating.VaniElementLocator;
import org.markysoft.vani.core.locating.factory.Annotations;
import org.markysoft.vani.core.locating.factory.ElementLocatorFactory;
import org.markysoft.vani.core.locating.factory.LocatorBuilderFactory;
import org.markysoft.vani.core.locating.locator.ByJQuery;
import org.markysoft.vani.core.util.ElementInterceptor;
import org.markysoft.vani.core.util.FieldTypeInfo;
import org.markysoft.vani.core.util.VaniReflectionUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class LocatorFieldCallback implements FieldCallback {
	private static Log logger = LogFactory.getLog(LocatorFieldCallback.class);
	private static final List<Class<? extends Annotation>> APPLICABLE_ANNOTATIONS = Arrays.asList(FindBy.class,
			FindBys.class, FindAll.class);

	private Object bean;
	private VaniContext vaniContext;
	private ElementLocatorFactory elementLocatorFactory;
	private VaniReflectionUtil reflectionUtil;
	protected ConfigurableListableBeanFactory configurableBeanFactory;
	protected LocatorBuilderFactory locatorBuilderFactory;

	public LocatorFieldCallback(ConfigurableListableBeanFactory configurableBeanFactory, Object bean,
			VaniContext vaniContext) {
		this.configurableBeanFactory = configurableBeanFactory;
		this.bean = bean;
		this.vaniContext = vaniContext;
		reflectionUtil = vaniContext.getAppContext().getBean(VaniReflectionUtil.class);
		elementLocatorFactory = vaniContext.getAppContext().getBean(ElementLocatorFactory.class);
		locatorBuilderFactory = vaniContext.getAppContext().getBean(LocatorBuilderFactory.class);
	}

	@Override
	public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
		Object beanInstance = null;
		for (Class<? extends Annotation> annotationClass : APPLICABLE_ANNOTATIONS) {
			if (field.isAnnotationPresent(annotationClass)) {
				beanInstance = handle(annotationClass, field);
				break;
			}
		}
		Annotation customLocatorAnnotation = reflectionUtil.getAnnotatedAnnotation(field, LocatorBuilderClass.class);
		if (customLocatorAnnotation != null) {
			beanInstance = handle(customLocatorAnnotation, field);
		}
		if (beanInstance != null) {
			ReflectionUtils.makeAccessible(field);
			field.set(bean, beanInstance);
		}
	}

	protected <T extends Annotation> Object handle(Class<T> annotationClass, Field field) {
		T annotation = field.getDeclaredAnnotation(annotationClass);
		return handle(annotation, field);
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Annotation> Object handle(T annotation, Field field) {
		By by = from(annotation, field);

		FieldTypeInfo fieldTypeInfo = reflectionUtil.getFieldTypeInfo(field, bean);
		boolean shouldCache = (field.getAnnotation(CacheLookup.class) != null) || by instanceof ByJQuery;
		SearchContext searchContext = getSearchContext();
		VaniElementLocator elementLocator = elementLocatorFactory.createLocator(fieldTypeInfo, searchContext, by,
				shouldCache);

		Class<?> type = fieldTypeInfo.getFieldType();
		Object proxy = createProxy(type, elementLocator, field);
		return proxy;
	}

	protected SearchContext getSearchContext() {
		SearchContext result = null;

		if (bean instanceof RegionObject) {
			result = ((RegionObject) bean).getWebDriver();
			if (bean instanceof FragmentObject) {
				result = ((FragmentObject) bean).getRootElement();
			}
		} else {
			result = vaniContext.getAppContext().getBean(WebDriver.class);
		}

		return result;
	}

	/**
	 * method to create a proxy object for given field of specified class.
	 * 
	 * @param targetClass
	 * @param elementLocator
	 * @param field
	 * @return
	 * @throws UnresolvableLocatorException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <R> R createProxy(Class<?> targetClass, VaniElementLocator elementLocator, Field field)
			throws UnresolvableLocatorException {
		R result;
		try {
			//@formatter:off
			result = (R) new ByteBuddy()
					.subclass(targetClass)
					.method(ElementMatchers.any())
					.intercept(MethodDelegation.to(new ElementInterceptor(elementLocator)))
					.make()
					.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded()
					.newInstance();
			//@formatter:on
		} catch (Exception ex) {
			throw new UnresolvableLocatorException(
					"cannot resolve locator for class '" + targetClass + "' and field '" + field + "': " + ex, ex);
		}
		return result;
	}

	public <T extends Annotation> By from(T annotation, Field field) {
		LocatorBuilderClass locatorBuilderAnnotation = annotation.annotationType()
				.getDeclaredAnnotation(LocatorBuilderClass.class);
		if (locatorBuilderAnnotation == null) {
			return new Annotations(field, vaniContext).buildBy();
		} else {
			return fromCustom(annotation, field, locatorBuilderAnnotation);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T extends Annotation> By fromCustom(T annotation, Field field,
			LocatorBuilderClass locatorBuilderAnnotation) {
		By result = null;

		Class<? extends LocatorBuilder> builderClass = locatorBuilderAnnotation.value();
		LocatorBuilder builder = null;
		if (builderClass != null) {
			builder = locatorBuilderFactory.get(builderClass);
		} else {
			throw new UnableToLocateException(
					"No LocatorBuilder class specified for annotation '" + annotation + "' of field '" + field + "'");
		}

		result = builder.build(annotation);
		return result;
	}

}
