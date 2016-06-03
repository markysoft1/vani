package org.markysoft.vani.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.markysoft.vani.core.annotation.JavaScript;
import org.markysoft.vani.core.annotation.JavaScriptFunction;
import org.markysoft.vani.core.util.VaniReflectionUtil;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VaniReflectionUtilTest {
	private VaniReflectionUtil bean;

	@Before
	public void setUp() {
		bean = new VaniReflectionUtil();
	}

	/**
	 * tests {@link VaniReflectionUtil#getFirstParameterizedType(Field)} when
	 * provided field has no generic type.
	 * <p>
	 * As result, {@code NULL} must be returned, because specified field has no
	 * generic type.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetFirstParameterizedTypeWithNonGeneric() throws Exception {
		System.out.println("testGetFirstParameterizedTypeWithNonGeneric");

		Field field = TestingField.class.getDeclaredField("nonGeneric");

		Class<?> result = bean.getFirstParameterizedType(field);

		Assert.assertEquals("wrong result: ", null, result);
	}

	/**
	 * tests {@link VaniReflectionUtil#getFirstParameterizedType(Field)} when
	 * provided field has no generic type.
	 * <p>
	 * As result, {@code NULL} must be returned, because specified field has no
	 * generic type.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetFirstParameterizedTypeWithGeneric() throws Exception {
		System.out.println("testGetFirstParameterizedTypeWithGeneric");

		Field field = TestingField.class.getDeclaredField("genericList");

		Class<?> result = bean.getFirstParameterizedType(field);

		Assert.assertEquals("wrong result: ", String.class, result);
	}

	/**
	 * tests {@link VaniReflectionUtil#getElementTargetType(Field)} when
	 * provided field has no generic type.
	 * <p>
	 * As result, type of field must be returned, because specified field has no
	 * generic type.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetElementTargetTypeWithNonGeneric() throws Exception {
		System.out.println("testGetElementTargetTypeWithNonGeneric");

		Field field = TestingField.class.getDeclaredField("nonGeneric");

		Class<?> result = bean.getElementTargetType(field);

		Assert.assertEquals("wrong result: ", boolean.class, result);
	}

	/**
	 * tests {@link VaniReflectionUtil#getElementTargetType(Field)} when
	 * provided field has generic type.
	 * <p>
	 * As result, generic type of field must be returned.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetElementTargetTypeWithGeneric() throws Exception {
		System.out.println("testGetElementTargetTypeWithGeneric");

		Field field = TestingField.class.getDeclaredField("genericList");

		Class<?> result = bean.getElementTargetType(field);

		Assert.assertEquals("wrong result: ", String.class, result);
	}

	/**
	 * tests {@link VaniReflectionUtil#getElementTargetType(Field)} when
	 * provided field is parameterizable but no type is defined.
	 * <p>
	 * As result, {@code NULL} must be returned, because field has no generic
	 * type.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetElementTargetTypeWithoutGeneric() throws Exception {
		System.out.println("testGetElementTargetTypeWithoutGeneric");

		Field field = TestingField.class.getDeclaredField("listWithoutType");

		Class<?> result = bean.getElementTargetType(field);

		Assert.assertEquals("wrong result: ", null, result);
	}

	/**
	 * tests {@link VaniReflectionUtil#getField(Field)} when provided field is
	 * unknown.
	 * <p>
	 * As result, {@code NULL} must be returned, because field is unknown.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetFieldWithUnkown() throws Exception {
		System.out.println("testGetFieldWithUnkown");

		Field result = bean.getField("haha", TestingField.class);

		Assert.assertEquals("wrong result: ", null, result);
	}

	/**
	 * tests {@link VaniReflectionUtil#getField(Field)} when provided field is
	 * unknown and target class has super class.
	 * <p>
	 * As result, {@code NULL} must be returned, because field is unknown.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetFieldWithUnkownAndSuper() throws Exception {
		System.out.println("testGetFieldWithUnkownAndSuper");

		Field result = bean.getField("haha", TestingFieldImpl.class);

		Assert.assertEquals("wrong result: ", null, result);
	}

	/**
	 * tests {@link VaniReflectionUtil#getField(Field)} when provided field is
	 * contained by super class of target class.
	 * <p>
	 * As result, corresponding field must be returned.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetFieldOfSuper() throws Exception {
		System.out.println("testGetFieldOfSuper");

		Field result = bean.getField("nonGeneric", TestingFieldImpl.class);

		Assert.assertEquals("wrong result: ", TestingField.class.getDeclaredField("nonGeneric"), result);
	}

	/**
	 * tests {@link VaniReflectionUtil#getField(Field)} when provided field is
	 * contained by target class.
	 * <p>
	 * As result, corresponding field must be returned.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetField() throws Exception {
		System.out.println("testGetField");

		Field result = bean.getField("implString", TestingFieldImpl.class);

		Assert.assertEquals("wrong result: ", TestingFieldImpl.class.getDeclaredField("implString"), result);
	}

	/**
	 * tests
	 * {@link VaniReflectionUtil#getAnnotatedMethodsWith(Class, Class, Class)}
	 * when target class has multiple methods with and without target annotation
	 * and super class with and without annotations.
	 * <p>
	 * As result, only methods with provided annotation must be returned.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAnnotatedMethodsWith() throws Exception {
		System.out.println("testGetAnnotatedMethodsWith");

		Method[] expected = new Method[] { TestingMethodsImpl.class.getDeclaredMethod("methodImpl2"),
				TestingMethods.class.getDeclaredMethod("method2") };
		List<Method> result = bean.getAnnotatedMethodsWith(TestingMethodsImpl.class, JavaScriptFunction.class,
				String.class);

		Assert.assertArrayEquals("wrong result: ", expected, result.toArray());
	}

	/**
	 * tests {@link VaniReflectionUtil#getTypeAnnotation(Class, Class)} when
	 * target class has super class with looking annotation.
	 * <p>
	 * As result, annotation instance of super class must be returned.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetTypeAnnotationWithSuper() throws Exception {
		System.out.println("testGetTypeAnnotationWithSuper");

		JavaScript expected = TestingMethods.class.getDeclaredAnnotation(JavaScript.class);
		JavaScript result = bean.getTypeAnnotation(JavaScript.class, TestingMethodsImpl.class);

		Assert.assertEquals("wrong result: ", expected, result);
	}

	/**
	 * tests {@link VaniReflectionUtil#getTypeAnnotation(Class, Class)} when
	 * target class declares looking annotation.
	 * <p>
	 * As result, annotation instance of target class must be returned.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetTypeAnnotation() throws Exception {
		System.out.println("testGetTypeAnnotation");

		JavaScript expected = TEstingMethodsWithAnnotation.class.getDeclaredAnnotation(JavaScript.class);
		JavaScript result = bean.getTypeAnnotation(JavaScript.class, TestingMethodsImpl.class);

		Assert.assertEquals("wrong result: ", expected, result);
	}

	@JavaScript
	class TestingMethods {
		@JavaScriptFunction
		int method1() {
			return 5;
		}

		@JavaScriptFunction
		String method2() {
			return "5";
		}

		long method3() {
			return 5L;
		}
	}

	class TestingMethodsImpl extends TestingMethods {
		@JavaScriptFunction
		String methodImpl2() {
			return "";
		}

		@JavaScriptFunction
		boolean methodImpl3() {
			return true;
		}

		String methodImpl4() {
			return "";
		}
	}

	@JavaScript
	class TEstingMethodsWithAnnotation extends TestingMethods {
	}

	class TestingField {
		boolean nonGeneric = true;
		List<String> genericList;
		List listWithoutType;
	}

	class TestingFieldImpl extends TestingField {
		String implString;
	}
}
