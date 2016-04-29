package org.vani.core.util;

import java.lang.reflect.Field;

import org.openqa.selenium.WebDriver;
import org.vani.core.annotation.AjaxWait;
import org.vani.core.annotation.ContentWait;
import org.vani.core.locating.RegionObject;

public class FieldTypeInfo {
	private Field field;
	private Object bean;
	private Class<?> fieldType;
	private Class<?> firstGenericType;

	public FieldTypeInfo(Field field, Object bean, Class<?> fieldType, Class<?> firstGenericType) {
		super();
		this.field = field;
		this.bean = bean;
		this.fieldType = fieldType;
		this.firstGenericType = firstGenericType;
	}

	public Field getField() {
		return field;
	}

	public Object getBean() {
		return bean;
	}

	public boolean isGenericType() {
		return firstGenericType != null;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getFieldType() {
		return (Class<T>) fieldType;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getFirstGenericType() {
		return (Class<T>) firstGenericType;
	}

	public <T> Class<?> getTargetType() {
		return isGenericType() ? getFirstGenericType() : getFieldType();
	}

	public ContentWait getContentWait() {
		ContentWait result = null;
		if (field.isAnnotationPresent(ContentWait.class)) {
			result = field.getAnnotation(ContentWait.class);
		} else if (bean.getClass().isAnnotationPresent(ContentWait.class)) {
			result = bean.getClass().getAnnotation(ContentWait.class);
		}

		return result;
	}

	public AjaxWait getAjaxWait() {
		AjaxWait result = null;
		if (field.isAnnotationPresent(AjaxWait.class)) {
			result = field.getAnnotation(AjaxWait.class);
		} else if (bean.getClass().isAnnotationPresent(AjaxWait.class)) {
			result = bean.getClass().getAnnotation(AjaxWait.class);
		}

		return result;
	}

	/**
	 * method to get webDriver of underlying {@link RegionObject}.
	 * 
	 * @return returns value of {@code webDriver}-property of underlying
	 *         RegionObject or {@code NULL} if underlying object is not a
	 *         {@code RegionObject} or object has {@code NULL} as
	 *         {@code webDriver}
	 */
	public WebDriver getWebDriver() {
		WebDriver result = null;
		if (bean instanceof RegionObject) {
			result = ((RegionObject) bean).getWebDriver();
		}
		return result;
	}
}
