package org.vani.core.util;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.support.FindBy;
import org.vani.core.VaniContext;
import org.vani.core.locating.factory.AnnotationProxyFactory;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationInterceptorTest {
	private AnnotationInterceptor bean;

	@Mock
	protected VaniContext vaniContext;
	@Mock
	protected Annotation annotation;
	@Mock
	protected AnnotationProxyFactory annotationProxyFactory;

	@Mock
	protected FindBy findBy;
	@Mock
	protected FindBy wrappedFindBy;

	@Before
	public void setUp() {
		bean = new AnnotationInterceptor(annotation, vaniContext, annotationProxyFactory);
	}

	/**
	 * tests {@link AnnotationInterceptor#resolvePlaceholders(Object)} when
	 * {@code NULL} is specified as parameter.
	 * <p>
	 * As result, {@code NULL} must be returned, because {@code NULL} was
	 * specified.
	 * </p>
	 */
	@Test
	public void testResolvePlaceholdersWithNull() {
		System.out.println("testResolvePlaceholdersWithNull");

		Object result = bean.resolvePlaceholders(null);

		Assert.assertNull("result must be NULL", result);
	}

	/**
	 * tests {@link AnnotationInterceptor#resolvePlaceholders(Object)} when
	 * string is specified as parameter.
	 * <p>
	 * As result, resolved string must be returned.
	 * </p>
	 */
	@Test
	public void testResolvePlaceholdersWithString() {
		System.out.println("testResolvePlaceholdersWithString");

		when(vaniContext.resolveExpression("${placeholder}")).thenReturn("secret");

		Object result = bean.resolvePlaceholders("${placeholder}");

		verify(vaniContext, times(1)).resolveExpression("${placeholder}");
		verify(annotationProxyFactory, times(0)).createProxy(anyObject());
		Assert.assertEquals("wrong result", "secret", result);
	}

	/**
	 * tests {@link AnnotationInterceptor#resolvePlaceholders(Object)} when an
	 * array is specified as parameter.
	 * <p>
	 * As result, resolved array must be returned.
	 * </p>
	 */
	@Test
	public void testResolvePlaceholdersWithArray() {
		System.out.println("testResolvePlaceholdersWithArray");

		when(vaniContext.resolveExpression("${placeholder}")).thenReturn("my ");
		when(vaniContext.resolveExpression("${placeholder2}")).thenReturn("secret.");
		when(vaniContext.resolveExpression("third")).thenReturn("third");

		Object result = bean.resolvePlaceholders(new String[] { "${placeholder}", "${placeholder2}", "third" });

		verify(vaniContext, times(3)).resolveExpression(anyString());
		verify(annotationProxyFactory, times(0)).createProxy(anyObject());
		Assert.assertArrayEquals("wrong result", new String[] { "my ", "secret.", "third" }, (Object[]) result);
	}

	/**
	 * tests {@link AnnotationInterceptor#resolvePlaceholders(Object)} when
	 * annotation is specified as parameter.
	 * <p>
	 * As result, wrapped annotation must be returned.
	 * </p>
	 */
	@Test
	public void testResolvePlaceholdersWithAnnotation() {
		System.out.println("testResolvePlaceholdersWithAnnotation");

		when(annotationProxyFactory.createProxy(findBy)).thenReturn(wrappedFindBy);

		Object result = bean.resolvePlaceholders(findBy);

		verify(vaniContext, times(0)).resolveExpression(anyString());
		verify(annotationProxyFactory, times(1)).createProxy(findBy);
		Assert.assertEquals("wrong result", wrappedFindBy, result);
	}

}
