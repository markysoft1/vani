package org.vani.spring;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.vani.core.VaniContext;
import org.vani.core.annotation.Page;
import org.vani.core.annotation.Startpage;
import org.vani.core.locating.PageObject;
import org.vani.core.locating.factory.RegionFactory;

public class PageFieldCallback implements FieldCallback {
	private static Log logger = LogFactory.getLog(PageFieldCallback.class);

	private RegionFactory regionFactory;
	private Object bean;
	private Reflections reflections;
	private VaniContext vaniContext;

	public PageFieldCallback(Object bean, VaniContext vaniContext) {
		this.bean = bean;
		this.vaniContext = vaniContext;
		this.reflections = vaniContext.getReflections();
		this.regionFactory = vaniContext.getAppContext().getBean(RegionFactory.class);
	}

	@Override
	public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
		if (!field.isAnnotationPresent(Page.class) && !field.isAnnotationPresent(Startpage.class)) {
			return;
		}
		ReflectionUtils.makeAccessible(field);
		Class<?> fieldType = field.getType();
		Page pageAnnotation = field.getDeclaredAnnotation(Page.class);
		Startpage startpageAnnotation = field.getDeclaredAnnotation(Startpage.class);

		Class<?> targetType = getTargetClass(fieldType);

		if (targetType.isInterface() || Modifier.isAbstract(targetType.getModifiers())) {
			throw new IllegalStateException(
					"@Page and @Startpage annotation are not supported on interface or abstract types!");
		}

		Object beanInstance = regionFactory.create(targetType, pageAnnotation);
		field.set(bean, beanInstance);

		if (startpageAnnotation != null) {
			if (beanInstance instanceof PageObject) {
				((PageObject) beanInstance).to();
			} else {
				throw new IllegalStateException(
						"@Startpage annotation only supports types implementing vani's PageObject!");
			}
		}
	}

	private <T> Class<? extends T> getTargetClass(Class<T> type) {
		Class<? extends T> result = null;
		Class<? extends T> fallback = null;
		Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(type);
		if (!subTypes.isEmpty()) {
			for (Class<? extends T> subType : subTypes) {
				if (subType.isAnnotationPresent(Profile.class)) {
					Profile profileAnnotation = subType.getDeclaredAnnotation(Profile.class);
					if (vaniContext.isProfileEnabled(profileAnnotation)) {
						result = subType;
						break;
					}
				} else {
					fallback = subType;
				}
			}
		}

		if (result == null && fallback != null) {
			result = fallback;
		} else if (result == null && fallback == null) {
			result = type;
		}

		return result;
	}

}
