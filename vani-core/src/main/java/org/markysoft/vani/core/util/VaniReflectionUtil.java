package org.markysoft.vani.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.markysoft.vani.core.IllegalVaniFieldException;
import org.springframework.util.ReflectionUtils;

public class VaniReflectionUtil {
	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * method to get the first type of given parameterized field. If field has
	 * no generic type, {@code NULL} will be returned.
	 * 
	 * @param field
	 * @return returns first generic type of specified field or {@code NULL} if
	 *         not available.
	 */
	public Class<?> getFirstParameterizedType(Field field) {
		Type genericType = field.getGenericType();
		if (!(genericType instanceof ParameterizedType)) {
			return null;
		}

		Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
		return (Class<?>) listType;
	}

	/**
	 * method to get the target type of given field. This means if field is a
	 * list, it will return the generic type.
	 * 
	 * @param field
	 * @return returns generic type of list if field has the type {@link List},
	 *         else type of field will be returned. If list has no generic type,
	 *         result will be {@code NULL}.
	 */
	public Class<?> getElementTargetType(Field field) {
		Class<?> fieldType = field.getType();
		Class<?> elementTargetType = fieldType;
		if (List.class.isAssignableFrom(fieldType)) {
			elementTargetType = getFirstParameterizedType(field);
		}
		return elementTargetType;
	}

	/**
	 * This method will generate a {@link FieldTypeInfo} object with provided
	 * information.
	 * 
	 * @param field
	 * @param bean
	 * @return
	 */
	public FieldTypeInfo getFieldTypeInfo(Field field, Object bean) {
		Class<?> fieldType = field.getType();
		Class<?> firstGenericType = getFirstParameterizedType(field);

		FieldTypeInfo result = new FieldTypeInfo(field, bean, fieldType, firstGenericType);
		return result;
	}

	/**
	 * methods to get field with specified name of given class. It will also
	 * look in super classes.
	 * 
	 * @param name
	 * @param clazz
	 * @return returns desired field or {@code NULL} if no field with specified
	 *         name could be found in given class or any super class.
	 * @throws Exception
	 */
	public Field getField(String name, Class<?> clazz) throws Exception {
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException ex) {
			logger.debug("'" + clazz + "' has no field with name '" + name + "'");
		}
		if (field == null && !clazz.getSuperclass().equals(Object.class)) {
			logger.debug("look in super class '" + clazz.getSuperclass() + "'");
			field = getField(name, clazz.getSuperclass());
		}
		return field;
	}

	/**
	 * this method returns the first method of given class with specified
	 * annotation and return type. It will also include methods of super
	 * classes.
	 * 
	 * @param targetClass
	 * @param annotation
	 * @param returnType
	 *            returnType of method (NULL will ignore this criteria),
	 *            comparing will use equals-method
	 * @return returns first method of given class and its super classes with
	 *         given annotation and specified return type.
	 */
	public Method getAnnotatedMethodWith(Class<?> targetClass, Class<?> annotation, Class<?> returnType) {
		List<Method> methods = getAnnotatedMethodsWith(targetClass, annotation, returnType);
		return methods != null && !methods.isEmpty() ? methods.get(0) : null;
	}

	/**
	 * this method returns a list with all methods for given class with
	 * specified annotation and return type. It will also include methods of
	 * super classes.
	 * 
	 * @param targetClass
	 * @param annotation
	 * @param returnType
	 *            returnType of method (NULL will ignore this criteria),
	 *            comparing will use equals-method
	 * @return returns a list with methods of given class and its superclasses
	 *         with given annotation and specified return type.
	 */
	public List<Method> getAnnotatedMethodsWith(Class<?> targetClass, Class<?> annotation, Class<?> returnType) {
		List<Method> result = new ArrayList<>();
		while (targetClass != null && !Object.class.equals(targetClass)) {
			Method[] methods = targetClass.getDeclaredMethods();
			if (methods != null) {
				for (Method method : methods) {
					Annotation assignedAnnotation = method.getAnnotation((Class<? extends Annotation>) annotation);
					if (assignedAnnotation != null) {
						if (returnType != null) {
							if (!returnType.equals(method.getReturnType())) {
								continue;
							}
						}
						result.add(method);
					}
				}
			}

			targetClass = targetClass.getSuperclass();
		}

		return result;
	}

	/**
	 * this method returns target annotation instance of given class or one of
	 * its super classes.
	 * 
	 * @param annotationClazz
	 *            target annotation class
	 * @param clazz
	 *            target class
	 * @return returns instance of target annotation or {@code NULL} if not
	 *         exists in target class or one of its super classes
	 */
	public <T extends Annotation> T getTypeAnnotation(Class<T> annotationClazz, Class<?> clazz) {
		T result = null;
		if (clazz.isAnnotationPresent(annotationClazz)) {
			result = clazz.getDeclaredAnnotation(annotationClazz);
		}
		if (result == null && !clazz.getSuperclass().equals(Object.class)) {
			result = getTypeAnnotation(annotationClazz, clazz.getSuperclass());
		}
		return result;
	}

	public <T> void setFieldValue(String name, Object bean, T value)
			throws IllegalVaniFieldException, IllegalAccessException {
		if (bean == null) {
			logger.debug("ignore setting field value for field '" + name + "', because given bean instance is NULL!");
			return;
		}
		Field field;
		try {
			field = getField(name, bean.getClass());
		} catch (Exception ex) {
			throw new IllegalVaniFieldException("Target class '" + bean.getClass() + "' has no field '" + name
					+ "' for setting value '" + value + "'!", ex);
		}
		if (field != null) {
			ReflectionUtils.makeAccessible(field);
			field.set(bean, value);
		}
	}

	/**
	 * method to find first annotation of given field, which is annotated with
	 * specified annotation.
	 * 
	 * @param field
	 * @param annotationClass
	 * @return
	 */
	public Annotation getAnnotatedAnnotation(Field field, Class<? extends Annotation> annotationClass) {
		Annotation result = null;
		Annotation[] annotations = field.getDeclaredAnnotations();
		if (annotations != null) {
			for (Annotation anno : annotations) {
				if (anno.annotationType().isAnnotationPresent(annotationClass)) {
					result = anno;
				}
			}
		}
		return result;
	}

	public boolean hasMethodWithAnnotation(Class<?> targetClass, Class<? extends Annotation> annotationClass) {
		boolean result = getAnnotatedMethodWith(targetClass, annotationClass, null) != null;
		return result;
	}
}
